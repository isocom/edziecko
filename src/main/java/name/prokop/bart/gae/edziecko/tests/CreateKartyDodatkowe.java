package name.prokop.bart.gae.edziecko.tests;

import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class CreateKartyDodatkowe extends HttpServlet {

    private static final long serialVersionUID = 2620683133915360957L;

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
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", przedszkoleId));
            out.println(przedszkole);
            for (Dziecko d : przedszkole.getDzieci()) {
                for (Karta k : d.getKarty()) {
                    dzieci.put(k.getNumerKarty(), d);
                }
            }
            karty(out);
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
            out.println("Utworzono: " + przedszkole.getNazwa());
            out.println("dzieci.size(): " + przedszkole.getDzieci().size());
            // Uzytkownik u = new Uzytkownik();
            // u.setUser(new User("pp27@isocom.eu", "gmail.com"));
            // u.setPrzedszkoleKey(KeyFactory.createKey("Przedszkole",
            // przedszkole.getKey().getId()));
            // u.persist(pm);
        } catch (Exception ex) {
            pm.currentTransaction().rollback();
            out.println(ex.getMessage());
        } finally {
            pm.close();
        }
        out.close();
    }
    private HashMap<String, Dziecko> dzieci = new HashMap<String, Dziecko>();

    private void createKartaDoKarty(PrintWriter out, String staraKarta, String cardNo) {
        createKartaDoKarty(out, staraKarta, cardNo, StringToolbox.generateRandomStringId(10));
    }

    private void createKartaDoKarty(PrintWriter out, String staraKarta, String cardNo, String cardSerial) {
        if ("".equals(cardSerial)) {
            cardSerial = StringToolbox.generateRandomStringId(12);
        }
        Karta karta = new Karta(cardSerial, cardNo);
        Dziecko dziecko = dzieci.get(staraKarta);
        if (dziecko == null) {
            out.println("Nieznane dziecko");
            return;
        }
        dziecko.add(karta);
        out.println("Do karty " + staraKarta + " dziecko " + dziecko + " dodano: " + karta);
    }
    private static final Long przedszkoleId = 332902L;

    private void karty(PrintWriter out) {
        createKartaDoKarty(out, "9616 9903 1430 0020", "9616 9903 1430 1754");
        createKartaDoKarty(out, "9616 9903 1430 0053", "9616 9903 1430 1762");
        createKartaDoKarty(out, "9616 9903 1430 0061", "9616 9903 1430 1770");
        createKartaDoKarty(out, "9616 9903 1430 0095", "9616 9903 1430 1788");
        createKartaDoKarty(out, "9616 9903 1430 0103", "9616 9903 1430 1796");
        createKartaDoKarty(out, "9616 9903 1430 0103", "9616 9903 1430 1804");
        createKartaDoKarty(out, "9616 9903 1430 0129", "9616 9903 1430 1812");
        createKartaDoKarty(out, "9616 9903 1430 0129", "9616 9903 1430 1820");
        createKartaDoKarty(out, "9616 9903 1430 0137", "9616 9903 1430 1838");
        createKartaDoKarty(out, "9616 9903 1430 0137", "9616 9903 1430 1846");
        createKartaDoKarty(out, "9616 9903 1430 0145", "9616 9903 1430 1853");
        createKartaDoKarty(out, "9616 9903 1430 0152", "9616 9903 1430 1861");
        createKartaDoKarty(out, "9616 9903 1430 0178", "9616 9903 1430 1879");
        createKartaDoKarty(out, "9616 9903 1430 0178", "9616 9903 1430 1887");
        createKartaDoKarty(out, "9616 9903 1430 0186", "9616 9903 1430 1895");
        createKartaDoKarty(out, "9616 9903 1430 0202", "9616 9903 1430 1903");
        createKartaDoKarty(out, "9616 9903 1430 0210", "9616 9903 1430 1911");
        createKartaDoKarty(out, "9616 9903 1430 0228", "9616 9903 1430 1929");
        createKartaDoKarty(out, "9616 9903 1430 0244", "9616 9903 1430 1937");
        createKartaDoKarty(out, "9616 9903 1430 0251", "9616 9903 1430 1945");
        createKartaDoKarty(out, "9616 9903 1430 0293", "9616 9903 1430 1952");
        createKartaDoKarty(out, "9616 9903 1430 0319", "9616 9903 1430 1960");
        createKartaDoKarty(out, "9616 9903 1430 0327", "9616 9903 1430 1978");
        createKartaDoKarty(out, "9616 9903 1430 0327", "9616 9903 1430 1986");
        createKartaDoKarty(out, "9616 9903 1430 0335", "9616 9903 1430 1994");
        createKartaDoKarty(out, "9616 9903 1430 0350", "9616 9903 1430 2000");
        createKartaDoKarty(out, "9616 9903 1430 0400", "9616 9903 1430 2018");
        createKartaDoKarty(out, "9616 9903 1430 0434", "9616 9903 1430 2026");
        createKartaDoKarty(out, "9616 9903 1430 0459", "9616 9903 1430 2034");
        createKartaDoKarty(out, "9616 9903 1430 0483", "9616 9903 1430 2042");
        createKartaDoKarty(out, "9616 9903 1430 0491", "9616 9903 1430 2059");
        createKartaDoKarty(out, "9616 9903 1430 0491", "9616 9903 1430 2067");
        createKartaDoKarty(out, "9616 9903 1430 0509", "9616 9903 1430 2075");
        createKartaDoKarty(out, "9616 9903 1430 0558", "9616 9903 1430 2083");
        createKartaDoKarty(out, "9616 9903 1430 0566", "9616 9903 1430 2091");
        createKartaDoKarty(out, "9616 9903 1430 0590", "9616 9903 1430 2109");
        createKartaDoKarty(out, "9616 9903 1430 0616", "9616 9903 1430 2117");
        createKartaDoKarty(out, "9616 9903 1430 0632", "9616 9903 1430 2125");
        createKartaDoKarty(out, "9616 9903 1430 0665", "9616 9903 1430 2133");
        createKartaDoKarty(out, "9616 9903 1430 0699", "9616 9903 1430 2141");
        createKartaDoKarty(out, "9616 9903 1430 0715", "9616 9903 1430 2158");
        createKartaDoKarty(out, "9616 9903 1430 0731", "9616 9903 1430 2166");
        createKartaDoKarty(out, "9616 9903 1430 0749", "9616 9903 1430 2174");
        createKartaDoKarty(out, "9616 9903 1430 0756", "9616 9903 1430 2182");
        createKartaDoKarty(out, "9616 9903 1430 0764", "9616 9903 1430 2190");
        createKartaDoKarty(out, "9616 9903 1430 0772", "9616 9903 1430 2208");
        createKartaDoKarty(out, "9616 9903 1430 0798", "9616 9903 1430 2216");
        createKartaDoKarty(out, "9616 9903 1430 0814", "9616 9903 1430 2224");
        createKartaDoKarty(out, "9616 9903 1430 0830", "9616 9903 1430 2232");
        createKartaDoKarty(out, "9616 9903 1430 0855", "9616 9903 1430 2240");
        createKartaDoKarty(out, "9616 9903 1430 0897", "9616 9903 1430 2257");
        createKartaDoKarty(out, "9616 9903 1430 0905", "9616 9903 1430 2265");
        createKartaDoKarty(out, "9616 9903 1430 0921", "9616 9903 1430 2273");
        createKartaDoKarty(out, "9616 9903 1430 0947", "9616 9903 1430 2281");
        createKartaDoKarty(out, "9616 9903 1430 0954", "9616 9903 1430 2299");
        createKartaDoKarty(out, "9616 9903 1430 0988", "9616 9903 1430 2307");
        createKartaDoKarty(out, "9616 9903 1430 1028", "9616 9903 1430 2315");
        createKartaDoKarty(out, "9616 9903 1430 1036", "9616 9903 1430 2323");
        createKartaDoKarty(out, "9616 9903 1430 1044", "9616 9903 1430 2331");
        createKartaDoKarty(out, "9616 9903 1430 1051", "9616 9903 1430 2349");
        createKartaDoKarty(out, "9616 9903 1430 1085", "9616 9903 1430 2356");
        createKartaDoKarty(out, "9616 9903 1430 1093", "9616 9903 1430 2364");
        createKartaDoKarty(out, "9616 9903 1430 1135", "9616 9903 1430 2372");
        createKartaDoKarty(out, "9616 9903 1430 1150", "9616 9903 1430 2380");
        createKartaDoKarty(out, "9616 9903 1430 1168", "9616 9903 1430 2398");
        createKartaDoKarty(out, "9616 9903 1430 1168", "9616 9903 1430 2406");
        createKartaDoKarty(out, "9616 9903 1430 1176", "9616 9903 1430 2414");
        createKartaDoKarty(out, "9616 9903 1430 1200", "9616 9903 1430 2422");
        createKartaDoKarty(out, "9616 9903 1430 1226", "9616 9903 1430 2430");
        createKartaDoKarty(out, "9616 9903 1430 1242", "9616 9903 1430 2448");
        createKartaDoKarty(out, "9616 9903 1430 1283", "9616 9903 1430 2455");
        createKartaDoKarty(out, "9616 9903 1430 1309", "9616 9903 1430 2463");
        createKartaDoKarty(out, "9616 9903 1430 1317", "9616 9903 1430 2471");
        createKartaDoKarty(out, "9616 9903 1430 1325", "9616 9903 1430 2489");
        createKartaDoKarty(out, "9616 9903 1430 1341", "9616 9903 1430 2497");
        createKartaDoKarty(out, "9616 9903 1430 1341", "9616 9903 1430 2505");
        createKartaDoKarty(out, "9616 9903 1430 1358", "9616 9903 1430 2513");
        createKartaDoKarty(out, "9616 9903 1430 1366", "9616 9903 1430 2521");
        createKartaDoKarty(out, "9616 9903 1430 1374", "9616 9903 1430 2539");
        createKartaDoKarty(out, "9616 9903 1430 1374", "9616 9903 1430 2547");
        createKartaDoKarty(out, "9616 9903 1430 1382", "9616 9903 1430 2554");
        createKartaDoKarty(out, "9616 9903 1430 1408", "9616 9903 1430 2562");
        createKartaDoKarty(out, "9616 9903 1430 1457", "9616 9903 1430 2570");
        createKartaDoKarty(out, "9616 9903 1430 1465", "9616 9903 1430 2588");
        createKartaDoKarty(out, "9616 9903 1430 1473", "9616 9903 1430 2596");
        createKartaDoKarty(out, "9616 9903 1430 1481", "9616 9903 1430 2604");
        createKartaDoKarty(out, "9616 9903 1430 1499", "9616 9903 1430 2612");
        createKartaDoKarty(out, "9616 9903 1430 1515", "9616 9903 1430 2620");
        createKartaDoKarty(out, "9616 9903 1430 1531", "9616 9903 1430 2638");
        createKartaDoKarty(out, "9616 9903 1430 1531", "9616 9903 1430 2646");
        createKartaDoKarty(out, "9616 9903 1430 1549", "9616 9903 1430 2653");
        createKartaDoKarty(out, "9616 9903 1430 1556", "9616 9903 1430 2661");
        createKartaDoKarty(out, "9616 9903 1430 1564", "9616 9903 1430 2679");
        createKartaDoKarty(out, "9616 9903 1430 1572", "9616 9903 1430 2687");
        createKartaDoKarty(out, "9616 9903 1430 1572", "9616 9903 1430 2695");
    }
}