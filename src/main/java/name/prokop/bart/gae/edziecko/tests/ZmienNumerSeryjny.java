package name.prokop.bart.gae.edziecko.tests;

import com.google.appengine.api.datastore.Key;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

import com.google.appengine.api.datastore.KeyFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jdo.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ZmienNumerSeryjny extends HttpServlet {

    private static final long serialVersionUID = -6890403613412433577L;
    private Przedszkole przedszkole;
    private PrintWriter out;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        out = response.getWriter();

        try {
        } catch (Exception e) {
            e.printStackTrace(out);
        }



        String pwd = request.getParameter("passwd");
        if (!"TTSoft".equals(pwd)) {
            response.sendError(401);
            return;
        }

        Long przedszkoleId = 109001L;
        Long dziecko1 = 10001L;
        Long dziecko2 = 22001L;

        PersistenceManager pm = PMF.getPM();
        try {
            //pm.currentTransaction().begin();
            przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", przedszkoleId));
            przedszkole.populateLocalCache();

            out.println("Pobrano: " + przedszkole);

            Dziecko d1 = przedszkole.getDziecko(dziecko1);
            Dziecko d2 = przedszkole.getDziecko(dziecko2);
            out.println("Pobrano: " + d1);
            out.println("Pobrano: " + d2);

            Query q = pm.newQuery(Zdarzenie.class);
            q.setFilter("(dzieckoKey==dzieckoKeyParam) && (czasZdarzenia>fromParam) && (czasZdarzenia<toParam)");
            q.setOrdering("czasZdarzenia");
            q.declareParameters(Key.class.getName() + " dzieckoKeyParam, " + "java.util.Date fromParam, " + "java.util.Date toParam");
            try {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("dzieckoKeyParam", d2.getKey());
                params.put("fromParam", DateToolbox.encodeDate("20000101"));
                params.put("toParam", DateToolbox.encodeDate("20121231"));
                List<Zdarzenie> zz = (List<Zdarzenie>) q.executeWithMap(params);
                for (Zdarzenie z : zz) {
                    pm.currentTransaction().begin();
                    out.println(z);
                    z.setDzieckoKey(d1.getKey());
                    z.setKartaKey(d1.getKarty().iterator().next().getKey());
                    out.println(z);
                    pm.currentTransaction().commit();
                }
                pm.currentTransaction().begin();
                Karta karta = d2.getKarty().iterator().next();
                d2.getKarty().remove(karta);
//                pm.deletePersistent(karta);
//                d1.getKarty().add(karta);
//                pm.makePersistent(karta);
                pm.currentTransaction().commit();
            } finally {
                q.closeAll();
            }

            //pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
            out.println("Poprawiono: " + przedszkole);
        } catch (Exception ex) {
            pm.currentTransaction().rollback();
            out.println(ex.getMessage());
        } finally {
            pm.close();
        }
        out.close();
    }
}
