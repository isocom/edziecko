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
package name.prokop.bart.gae.edziecko.adminsrv;

import com.google.appengine.api.datastore.Key;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.Zaliczki;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.StringToolbox;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.pd;

public class UstawZaliczki extends HttpServlet {

    private static final long serialVersionUID = 7559398435030478125L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html><body>");
            out.println("<h2>Ustalenie zalicki</h2>");
            out.println("<form action=\"UstawZaliczki\" method=\"post\">");
            out.println("<h3>Zaliczka na opiekę</h3>");
            out.println("Ilość dni:<input type=\"text\" name=\"o_dni\" value=\"20\">");
            out.println("Zaliczkowa stawka za dzień:<input type=\"text\" name=\"o_stawka\" value=\"0.00\">");
            out.println("<h3>Zaliczka na zywienie</h3>");
            out.println("Ilość dni:<input type=\"text\" name=\"z_dni\" value=\"20\">");
            out.println("Zaliczkowa stawka za dzień:<input type=\"text\" name=\"z_stawka\" value=\"0.00\">");
            out.println("<input type=\"submit\" value=\"Zapisz Zaliczki\">");
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);
        Przedszkole przedszkole = eDzieckoRequest.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = eDzieckoRequest.retrieveRozliczenieMiesieczne();

        try {
            out.println("<html><body>");
            out.println("<h2>Wprowadzono następujące zaliczki na okres od " + DateToolbox.getFormatedDate("yyyy-MM-dd", eDzieckoRequest.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", eDzieckoRequest.decodeRokMiesiacTo()) + "</h2>");

            int oDni = Integer.parseInt(request.getParameter("o_dni"));
            double oStawka = pd(request.getParameter("o_stawka"));
            int zDni = Integer.parseInt(request.getParameter("z_dni"));
            double zStawka = pd(request.getParameter("z_stawka"));

            out.println("<table border=1 cellspacing=0 cellpadding=1>");
            out.println("<thead><tr>");
            out.println("<td>Dziecko</td>");
            out.println("<td>Pesel</td>");
            out.println("<td>Grupa</td>");
            out.println("<td>Zaliczka Opieka</td>");
            out.println("<td>Zaliczka Żywienie</td>");
            out.println("</tr></thead>");

            Zaliczki zaliczki = rozliczenieMiesieczne.getZaliczki();

            double oBO = 0.0, zBO = 0.0;
            for (Key dzieckoKey : rozliczenieMiesieczne.getListaDzieci()) {
                Dziecko d = przedszkole.getDziecko(dzieckoKey);
                zaliczki.setDniOpieka(d.getKey().getId(), oDni);
                zaliczki.setDniZywienie(d.getKey().getId(), zDni);
                zaliczki.setOpieka(d.getKey().getId(), oStawka * oDni * d.getRabat1AsFactor());
                zaliczki.setZywienie(d.getKey().getId(), zStawka * zDni * d.getRabat2AsFactor());

                out.print("<tr>");
                out.println("<td>" + d.getImieNazwisko() + "</td>");
                out.println("<td>" + d.getPeselAsString() + "</td>");
                out.println("<td>" + d.getGrupaAsString() + "</td>");
                double v = rozliczenieMiesieczne.getZaliczki().getOpieka(d.getKey().getId());
                oBO += v;
                out.println("<td>" + StringToolbox.d2s(v) + "</td>");
                v = rozliczenieMiesieczne.getZaliczki().getZywienie(d.getKey().getId());
                zBO += v;
                out.println("<td>" + StringToolbox.d2s(v) + "</td>");
                out.print("</tr>");
            }
            IC.INSTANCE.replace(rozliczenieMiesieczne);

            out.print("<tr>");
            out.println("<td>RAZEM:</td>");
            out.println("<td></td>");
            out.println("<td></td>");
            out.println("<td>" + StringToolbox.d2s(oBO) + "</td>");
            out.println("<td>" + StringToolbox.d2s(zBO) + "</td>");
            out.print("</tr>");

            out.println("</table>");

            out.println("<a href=\"Rozliczenie\">Powrót do rozliczeń</a>");
            out.print("</body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }
}
