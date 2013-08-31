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
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.pd;

public class WprowadzBO extends EDzieckoServlet {

    private static final long serialVersionUID = -6881887892079605749L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();

        response.println("<h1>BO okresu od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()) + "</h1>");
        response.println("Proszę wprowadzić ze znakiem plus dla nadpłat i ze znakiem minus dla zaległości.");
        response.println("<form action=\"WprowadzBO\" method=\"post\">");
        response.println("<input type=\"submit\" value=\"Zapisz Bilans otwarcia\">");

        response.println("<table>");
        response.println("<thead><tr>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>BO Opieka</td>");
        response.println("<td>BO Żywienie</td>");
        response.println("</tr></thead>");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            response.println("<tr>");
            Dziecko d = przedszkole.getDziecko(dzieckoKey);
            response.println("<td>" + d.getImieNazwisko() + "</td>");
            response.println("<td>" + (d.getPesel() != null ? d.getPesel() : "") + "</td>");
            response.println("<td>" + (d.getGrupa() != null ? d.getGrupa().getCategory() : "") + "</td>");
            response.println("<td><input type=\"text\" name=\"O_" + d.getKey().getId() + "\" value=\"" + StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().getOpieka(d.getKey().getId())) + "\"></td>");
            response.println("<td><input type=\"text\" name=\"Z_" + d.getKey().getId() + "\" value=\"" + StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().getZywienie(d.getKey().getId())) + "\"></td>");
            response.println("</tr>");
        }

        response.println("</table><input type=\"submit\" value=\"Zapisz BO\">");
        response.println("</form>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();

        response.println("<h1>BO okresu od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()) + "</h1>");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key k : listaDzieci) {
            try {
                double v = pd(request.getParameter("O_" + k.getId()));
                rozliczenieMiesieczne.getBilansOtwarcia().setOpieka(k.getId(), v);
            } catch (Exception e) {
            }
            try {
                double v = pd(request.getParameter("Z_" + k.getId()));
                rozliczenieMiesieczne.getBilansOtwarcia().setZywienie(k.getId(), v);
            } catch (Exception e) {
            }
        }
        IC.INSTANCE.replace(rozliczenieMiesieczne);

        response.println("<table>");
        response.println("<thead><tr>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>BO Opieka</td>");
        response.println("<td>BO Żywienie</td>");
        response.println("</tr></thead>");

        double oBO = 0.0, zBO = 0.0;
        for (Key k : listaDzieci) {
            response.println("<tr>");
            Dziecko d = przedszkole.getDziecko(k);
            response.println("<td>", d.getImieNazwisko(), "</td>");
            response.println("<td>", d.getPesel() != null ? d.getPesel() : "", "</td>");
            response.println("<td>", d.getGrupa() != null ? d.getGrupa().getCategory() : "", "</td>");
            double v = rozliczenieMiesieczne.getBilansOtwarcia().getOpieka(k.getId());
            oBO += v;
            response.println("<td>", StringToolbox.d2h(v), "</td>");
            v = rozliczenieMiesieczne.getBilansOtwarcia().getZywienie(k.getId());
            zBO += v;
            response.println("<td>", StringToolbox.d2h(v), "</td>");
            response.println("</tr>");
        }

        response.println("<tr>");
        response.println("<td>RAZEM:</td>");
        response.println("<td></td>");
        response.println("<td></td>");
        response.println("<td>", StringToolbox.d2h(oBO), "</td>");
        response.println("<td>", StringToolbox.d2h(zBO), "</td>");
        response.println("</tr>");

        response.println("</table>");
        response.println("<a href=\"Rozliczenie\">Powrót do rozliczeń</a>");
    }

    @Override
    protected String getTitle() {
        return "Wprowad bilans otwarcia";
    }

    @Override
    protected PDFType getPDFType() {
        return PDFType.BO;
    }

    @Override
    protected XLSType getXLSType() {
        return XLSType.BO;
    }
}
