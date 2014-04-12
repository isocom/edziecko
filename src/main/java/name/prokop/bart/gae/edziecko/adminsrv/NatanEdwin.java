package name.prokop.bart.gae.edziecko.adminsrv;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.TypZdarzenia;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.bol.cacheable.CacheableZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class NatanEdwin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        String cn = request.getParameter("cn");
        if (cn == null || cn.trim().length() == 0) {
            writer.print("Podaj numer karty.");
            return;
        }
        cn = cn.trim();
        cn = StringToolbox.cardNumberPretty(StringToolbox.cardNumberCompress(cn));

        List<String> cnss = new ArrayList<String>();
        String cns = request.getParameter("cns");
        if (cns != null && cns.trim().length() != 0) {
            for (String s : cns.split(",")) {
                cnss.add(StringToolbox.cardNumberPretty(StringToolbox.cardNumberCompress(s)));
            }
        }

        Date date = new Date();
        String s = DateToolbox.getFormatedDate("yyyyMMddHHmm", date);
        date = DateToolbox.parseDateChecked("yyyyMMddHHmm", s);
        process(cn, cnss, date, writer);
    }

    private void process(String cardNo, List<String> cnss, Date date, PrintWriter writer) {
        try {
            addCard(cardNo, cnss);
        } catch (Throwable t) {
            writer.print("BLAD:" + t.getMessage());
            return;
        }

        Karta karta = IC.INSTANCE.getKartaByCN(cardNo);
        if (karta == null) {
            writer.print("BRAK:" + cardNo);
            return;
        }

        Przedszkole przedszkole = karta.getDziecko().getPrzedszkole();
        PersistenceManager pm = PMF.getPM();

        try {
            Zdarzenie zdarzenie = new Zdarzenie();
            karta = przedszkole.getKartaByCN(cardNo);
            zdarzenie.setKartaKey(karta.getKey());
            zdarzenie.setDzieckoKey(karta.getDziecko().getKey());
            zdarzenie.setPrzedszkoleKey(karta.getDziecko().getPrzedszkole().getKey());
            zdarzenie.setCzasZdarzenia(date);
            zdarzenie.setTypZdarzenia(TypZdarzenia.KontrolaDostepu);
            zdarzenie.persist(pm);

            String key = ZbiorZdarzen.buildID(karta.getDziecko().getPrzedszkole().getKey(), Integer.parseInt(DateToolbox.getFormatedDate("yyyyMM", date)));
            CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
            if (czz != null) {
                czz = new CacheableZbiorZdarzen(czz, zdarzenie);
                IC.INSTANCE.store(key, czz);
            }
            writer.print("Dodano:" + karta.getDziecko().getKey().getId());
        } finally {
            pm.close();
        }
    }

    private void addCard(final String cardNo, List<String> cnss) {
        Karta karta = IC.INSTANCE.getKartaByCN(cardNo);
        if (karta != null) {
            return;
        }

        for (String s : cnss) {
            karta = IC.INSTANCE.getKartaByCN(s);
            if (karta != null) {
                break;
            }
        }
        if (karta != null) {
            long przedszkoleId = karta.getDziecko().getPrzedszkole().getKey().getId();
            long dzieckoId = karta.getDziecko().getKey().getId();
            addAdditionalCard(przedszkoleId, dzieckoId, cardNo);
        } else {
        }
    }

    protected void addAdditionalCard(long przedszkoleId, long dzieckoId, String cn) {
        String sn = "EX" + StringToolbox.generateRandomStringId(8);
        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", przedszkoleId));
            Dziecko dziecko = pm.getObjectById(Dziecko.class, KeyFactory.createKey(przedszkole.getKey(), "Dziecko", dzieckoId));
            dziecko.add(new Karta(sn, cn));
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
        } finally {
            if (pm.currentTransaction().isActive()) {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    protected void addNoweDziecko(long przedszkoleId, String imienazwisko, String cn) {
        String sn = "EX" + StringToolbox.generateRandomStringId(8);
        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", przedszkoleId));
            Dziecko dziecko = new Dziecko(imienazwisko);
            dziecko.setGrupa(new Category("N/D"));
            dziecko.add(new Karta(sn, cn));
            przedszkole.add(dziecko);
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
        } finally {
            if (pm.currentTransaction().isActive()) {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    private String process1(String cardNo, List<String> cnss, Date date) {
        Karta karta = IC.INSTANCE.getKartaByCN(cardNo);
        if (karta == null) {
            for (String s : cnss) {
                karta = IC.INSTANCE.getKartaByCN(s);
                if (karta != null) {
                    break;
                }
            }
            if (karta == null) {
                return "BRAK:" + cardNo;
            }
        }
        Przedszkole przedszkole = karta.getDziecko().getPrzedszkole();
        PersistenceManager pm = PMF.getPM();

        try {
            Zdarzenie zdarzenie = new Zdarzenie();
            karta = przedszkole.getKartaByCN(cardNo);
            zdarzenie.setKartaKey(karta.getKey());
            zdarzenie.setDzieckoKey(karta.getDziecko().getKey());
            zdarzenie.setPrzedszkoleKey(karta.getDziecko().getPrzedszkole().getKey());
            zdarzenie.setCzasZdarzenia(date);
            zdarzenie.setTypZdarzenia(TypZdarzenia.KontrolaDostepu);
            zdarzenie.persist(pm);

            String key = ZbiorZdarzen.buildID(karta.getDziecko().getPrzedszkole().getKey(), Integer.parseInt(DateToolbox.getFormatedDate("yyyyMM", date)));
            CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
            if (czz != null) {
                czz = new CacheableZbiorZdarzen(czz, zdarzenie);
                IC.INSTANCE.store(key, czz);
            }
            return "Dodano:" + karta.getDziecko().getKey().getId();
        } finally {
            pm.close();
        }
    }
}
