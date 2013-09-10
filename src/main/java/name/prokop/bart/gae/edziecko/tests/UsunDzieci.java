package name.prokop.bart.gae.edziecko.tests;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

public class UsunDzieci extends HttpServlet {

    private static final long serialVersionUID = -2415533117587394710L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pwd = request.getParameter("passwd");
        if (!"TTSoft".equals(pwd)) {
            response.sendError(401);
            return;
        }

        response.setContentType("text/plain;charset=UTF-8");
        Long id = Long.parseLong(request.getParameter("przedszkoleId"));
        PrintWriter out = response.getWriter();

        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", id));
            out.println(przedszkole);
            out.println("dzieci.size: " + przedszkole.getDzieci().size());
            Dziecko dziecko = new Dziecko("Zaległości");
            dziecko.setGrupa(new Category("N/D"));
            przedszkole.add(dziecko);
            dziecko = new Dziecko("Nadpłaty");
            dziecko.setGrupa(new Category("N/D"));
            przedszkole.add(dziecko);
            out.println("dzieci.size: " + przedszkole.getDzieci().size());
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
        } catch (Exception ex) {
            ex.printStackTrace(out);
        } finally {
            if (pm.currentTransaction().isActive()) {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
        out.close();
    }

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
        HashSet<Dziecko> dousuniecia = new HashSet<Dziecko>();
        Long id = Long.parseLong(request.getParameter("przedszkoleId"));
        pm.currentTransaction().begin();
        Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, id);
        for (Dziecko d : przedszkole.getDzieci()) {
            for (Karta k : d.getKarty()) {
                if (k.getNumerSeryjny().startsWith("TMP") && d.getKarty().size() == 1) {
                    dousuniecia.add(d);
                    out.println("DO USUNIECIA: " + d);
                }
            }
        }
        for (Dziecko d : dousuniecia) {
            przedszkole.getDzieci().remove(d);
        }
        pm.currentTransaction().commit();

        for (Dziecko d : dousuniecia) {
            for (Karta k : d.getKarty()) {
                pm.deletePersistent(k);
                out.println("Usunieto: " + k);
            }
            pm.deletePersistent(d);
            out.println("Usunieto: " + d);
        }
        pm.close();
        out.close();
    }
}
