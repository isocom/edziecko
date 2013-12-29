package name.prokop.bart.gae.edziecko.adminsrv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        Date date = new Date();

        String s = DateToolbox.getFormatedDate("yyyyMMddHHmm", date);
        date = DateToolbox.parseDateChecked("yyyyMMddHHmm", s);
        writer.print(process(cn, date));
    }

    private String process(String cardNo, Date date) {
        Karta karta = IC.INSTANCE.getKartaByCN(cardNo);
        if (karta == null) {
            return "BRAK:" + cardNo;
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
