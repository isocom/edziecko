/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package name.prokop.bart.gae.edziecko.ola;

import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;

public class DodajKarte extends EDzieckoServlet {

    private static final long serialVersionUID = -8366576781205779068L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        String d = request.getParameter("d");
        if (d == null || d.trim().equals("")) {
            response.println("<h1>Wybierz dziecko ktoremu dodajesz karte</h1>");
            Przedszkole przedszkole = request.getPrzedszkole();
            List<Dziecko> dzieci = new ArrayList<Dziecko>(przedszkole.getDzieci());
            Collections.sort(dzieci, new Comparator<Dziecko>() {
                @Override
                public int compare(Dziecko o1, Dziecko o2) {
                    return o1.getImieNazwiskoAsString().compareTo(o2.getImieNazwiskoAsString());
                }
            });
            for (Dziecko dziecko : dzieci) {
                response.print("<a href=\"DodajKarte?d=" + dziecko.getKey().getId() + "\">");
                response.print(dziecko.toString());
                response.print(", ");
                response.print(dziecko.getGrupaAsString());
                response.println("</a><br>");
            }
        } else {
            Przedszkole przedszkole = request.getPrzedszkole();
            Dziecko dziecko = przedszkole.getDziecko(KeyFactory.createKey(przedszkole.getKey(), "Dziecko", Long.parseLong(d)));
            response.println("<h1>Karta dla: " + dziecko + "</h1>");
            response.println("<form action=\"DodajKarte\" method=\"post\"><table>");
            response.println("<tr><td>Klucz dziecka:</td><td><input type=\"text\" name=\"d\" value=\"" + dziecko.getKey().getId() + "\"></td></tr>");
            response.println("<tr><td>Numer karty:</td><td><input type=\"text\" name=\"cn\" value=\"" + DodajDziecko.calculateNextCardNumber(przedszkole) + "\"></td></tr>");
            response.println("<tr><td>Numer seryjny:</td><td><input type=\"text\" name=\"sn\" value=\"TMP9" + StringToolbox.generateRandomStringId(8) + "\"></td></tr>");
            response.println("<tr><td><td colspan=\"2\"><input type=\"submit\" value=\"Zrób\"></td></tr>");
            response.println("</table></form>");
        }
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        String d = request.getParameter("d");
        String cn = request.getParameter("cn").trim();
        String sn = request.getParameter("sn").trim();

        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, request.getPrzedszkoleKey());
            response.println("Dzieci.size: " + przedszkole.getDzieci().size() + "<br>");
            Dziecko dziecko = pm.getObjectById(Dziecko.class, KeyFactory.createKey(przedszkole.getKey(), "Dziecko", Long.parseLong(d)));
            response.println("dziecko.karty.size: " + dziecko.getKarty().size() + "<br>");
            dziecko.add(new Karta(sn, cn));
            response.println("dziecko.karty.size: " + dziecko.getKarty().size() + "<br>");
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
            response.println("Zmieniono: " + dziecko + "<br>");
            for (Karta k : dziecko.getKarty()) {
                response.println(" - " + k + "<br>");
            }
        } finally {
            if (pm.currentTransaction().isActive()) {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    @Override
    protected String getTitle() {
        return "Dodaj dodatkową kartę";
    }

    @Override
    protected PDFType getPDFType() {
        return null;
    }

    @Override
    protected XLSType getXLSType() {
        return null;
    }
}
