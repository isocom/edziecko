package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.Uzytkownik;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.PMF;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.users.User;

public class CreatePrzedszkole extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1914119596708215352L;

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
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = new Przedszkole();
            przedszkole.setNazwa("Przedszkole Państwowe nr 39");
            przedszkole.setAdres(new PostalAddress("35-xxx Rzeszow"));
            przedszkole.setNip("287-314-11-22");
            przedszkole.setTerminLicencji(DateToolbox.encodeDate("20141130"));
//            przedszkole.setKalkulatorDnia("name.prokop.bart.gae.edziecko.reports.no.biskupiec.Biskupiec");
//            przedszkole.setKalkulatorDnia("name.prokop.bart.gae.edziecko.reports.rz.swilcza.Mrowla");
            przedszkole.setKalkulatorDnia("name.prokop.bart.gae.edziecko.reports.rz.rzeszow.Rzeszow");
            for (Dziecko d : listaDzieci()) {
                przedszkole.add(d);
            }
            przedszkole.persist(pm);
            pm.currentTransaction().commit();
            out.println("Utworzono: " + przedszkole.getNazwa());
            out.println("dzieci.size(): " + przedszkole.getDzieci().size());
            out.println("DUMP:\n" + przedszkole);

            Uzytkownik u = new Uzytkownik();
            u.setUser(new User("lubenia@isocom.eu", "gmail.com"));
            u.setPrzedszkoleKey(KeyFactory.createKey("Przedszkole", przedszkole.getKey().getId()));
            u.persist(pm);
        } catch (Exception ex) {
            pm.currentTransaction().rollback();
            out.println(ex.getMessage());
        } finally {
            pm.close();
        }
        out.close();
    }

    private Dziecko createDziecko(String imieNazwisko, String grupa, String cardNo, String cardSerial) {
        Dziecko dziecko = new Dziecko(imieNazwisko.trim());
        if ("".equals(grupa)) {
            grupa = "N/D";
        }
        dziecko.setGrupa(new Category(grupa));
        if ("".equals(cardSerial)) {
            cardSerial = StringToolbox.generateRandomStringId(10);
        }
        Karta karta = new Karta(cardSerial.trim(), cardNo.trim());
        dziecko.add(karta);
        return dziecko;
    }

    private List<Dziecko> listaDzieci() {
        List<Dziecko> retVal = new ArrayList<Dziecko>();
        retVal.add(createDziecko("Pierwsze Dziecko", "I", "9616 9907 4440 0010", "12345678"));
        return retVal;
    }
}
