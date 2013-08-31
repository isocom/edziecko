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

import java.io.IOException;
import java.io.PrintWriter;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 * Klasa do ustawiania numeru seryjnego. Na podstawie numeru karty przypisywany
 * jest numer seryjny
 *
 * @author bart
 *
 */
public class UtworzKarteDodatkowa extends HttpServlet {

    private static final long serialVersionUID = 6301620533638329308L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("cn1") != null && request.getParameter("cn2") != null) {
            doPost(request, response);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html><body>");
            out.println("<h2>Utworzenie karty dodatkowej do karty istniejącej</h2>");
            out.println("<form action=\"UtworzKarteDodatkowa\" method=\"post\">");
            out.println("Karta podstawowa: <input type=\"text\" name=\"cn1\" value=\"numer istniejący\">");
            out.println("Karta dodatkowa:  <input type=\"text\" name=\"cn2\" value=\"numer nowej kart\">");
            out.println("<input type=\"submit\" value=\"Twórz kartę\">");
            out.println("</form>");
            out.print("</body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String cn1 = request.getParameter("cn1").trim();
        cn1 = StringToolbox.cardNumberCompress(cn1);
        cn1 = StringToolbox.cardNumberPretty(cn1);
        String cn2 = request.getParameter("cn2").trim();
        cn2 = StringToolbox.cardNumberCompress(cn2);
        cn2 = StringToolbox.cardNumberPretty(cn2);
        out.println("cn1   : " + cn1);
        out.println("cn2   : " + cn2);

        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Karta karta1 = Karta.findByCN(pm, cn1);
            Karta karta2 = Karta.findByCN(pm, cn2);
            if (karta2 != null) {
                throw new IllegalStateException("Istnieje już karta: " + karta2);
            }

            if (karta1 != null) {
                out.println("Znaleziono: " + karta1);
                Dziecko dziecko = karta1.getDziecko();
                out.println("Dziecko: " + dziecko);
                karta2 = new Karta(StringToolbox.generateRandomStringId(12), cn2);
                dziecko.add(karta2);
                out.println("Dodano:" + karta2);
            } else {
                out.println("Nie znaleziono karty o numerze: " + cn1);
            }
            pm.currentTransaction().commit();
            out.println("Po  dodaniu: " + karta2);
            IC.INSTANCE.replace(karta1.getDziecko().getPrzedszkole());
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
}
