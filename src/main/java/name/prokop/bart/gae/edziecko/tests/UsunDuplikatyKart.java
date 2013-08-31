package name.prokop.bart.gae.edziecko.tests;

import com.google.appengine.api.datastore.Key;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

public class UsunDuplikatyKart extends HttpServlet {

    private static final long serialVersionUID = 244189441130653936L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pwd = request.getParameter("passwd");
        if (!"TTSoft".equals(pwd)) {
            response.sendError(401);
            return;
        }

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        PersistenceManager pm = PMF.getPM();
        Long pId = Long.parseLong(request.getParameter("pId"));
//        pm.currentTransaction().begin();
        Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, pId);
        Iterator<Dziecko> id = przedszkole.getDzieci().iterator();
        while (id.hasNext()) {
            boolean delete = false;
            Dziecko d = id.next();
            Iterator<Karta> ik = d.getKarty().iterator();
            while (ik.hasNext()) {
                Karta k = ik.next();
                //out.println("Badam: " + k);
                if (k.getNumerSeryjny().startsWith("TMP")) {
                    out.println("USUWAM: " + k);
                    delete = true;
                    ik.remove();
                }
            }
            if (delete) {
                out.println("USUWAM: " + d);
                id.remove();
            }
        }
//        pm.currentTransaction().commit();
        IC.INSTANCE.replace(przedszkole);

        pm.close();
        out.close();
    }

    private void xxx(PrintWriter out) {
        PersistenceManager pm = PMF.getPM();
        Long pId = 108001L;
        pm.currentTransaction().begin();
        Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, pId);
        for (Dziecko d : przedszkole.getDzieci()) {
            Karta p = new Karta();
            p.setNumerKarty("DUPAJASIA");
            Karta usun = null;
            for (Karta k : d.getKarty()) {
                if (p.getNumerKarty().equals(k.getNumerKarty())) {
                    out.println("Znalazlem duplikat: " + k);
                    List<Zdarzenie> zp = xxx(pm, p);
                    List<Zdarzenie> zk = xxx(pm, k);
                    out.println(zp.size() + " " + p);
                    out.println(zk.size() + " " + k);
                    if (zp.size() == 0) {
                        usun = p;
                    }
                    if (zk.size() == 0) {
                        usun = k;
                    }
                }
                p = k;
            }
            if (usun != null) {
                System.out.println("usuwam: " + usun);
                d.getKarty().remove(usun);
            }
        }
        pm.currentTransaction().commit();
        IC.INSTANCE.replace(przedszkole);

        pm.close();
    }

    private List<Zdarzenie> xxx(PersistenceManager pm, Karta k) {
        Query q = pm.newQuery(Zdarzenie.class);
        q.setFilter("(kartaKey==kartaKeyParam)");
        q.declareParameters(Key.class.getName() + " kartaKeyParam");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("kartaKeyParam", k.getKey());
        List<Zdarzenie> zp = (List<Zdarzenie>) q.executeWithMap(params);
        q.closeAll();
        return zp;
    }
}
