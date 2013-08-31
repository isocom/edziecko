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
public class UstawNumerSeryjny extends HttpServlet {

    private static final long serialVersionUID = 8899924144845039187L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("cn") != null && request.getParameter("sn") != null) {
            doPost(request, response);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html><body>");
            out.println("<h2>Przypisanie numeru seryjnego karty do numeru logicznego karty</h2>");
            out.println("<form action=\"UstawNumerSeryjny\" method=\"post\">");
            out.println("Numer karty:<input type=\"text\" name=\"cn\" value=\"---\">");
            out.println("Num seryjny:<input type=\"text\" name=\"sn\" value=\"" + StringToolbox.generateRandomStringId(10) + "\">");
            out.println("<input type=\"submit\" value=\"Ustaw\">");
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

        String cn = request.getParameter("cn").trim();
        cn = StringToolbox.cardNumberCompress(cn);
        cn = StringToolbox.cardNumberPretty(cn);
        String sn = request.getParameter("sn").trim();
        out.println("cn   : " + cn);
        out.println("sn   : " + sn);

        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Karta karta = Karta.findByCN(pm, cn);
            if (karta != null) {
                out.println("Przed: " + karta);
                karta.setNumerSeryjny(sn);
                out.println("Po  1: " + karta);
            } else {
                out.println("Nie znaleziono karty");
            }
            pm.currentTransaction().commit();
            if (karta != null) {
                out.println("Po  2: " + karta);
                IC.INSTANCE.replace(karta.getDziecko().getPrzedszkole());
            }
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
