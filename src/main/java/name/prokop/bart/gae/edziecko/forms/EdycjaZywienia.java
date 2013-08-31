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
package name.prokop.bart.gae.edziecko.forms;

import com.google.appengine.api.datastore.Key;
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.Zuzycie;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.pi;

public class EdycjaZywienia extends EDzieckoServlet {

    private static final long serialVersionUID = -7113516418722784369L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();

        response.println("<h1>Edycja kosztów żywienia w okresie od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()) + "</h1>");
        response.println("Proszę wprowadzić (zmodyfikować) ilość dniówek żywienia. Kwota kosztu zostanie wyliczona automatycznie.");
        response.println("<form action=\"EdycjaZywienia\" method=\"post\">");
        response.println("<input type=\"submit\" value=\"Zapisz zmiany\">");

        response.println("<table>");
        response.println("<thead><tr>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>Ilość</td>");
        response.println("<td>Koszt</td>");
        response.println("<td>Nowa ilość dni</td>");
        response.println("</tr></thead>");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            response.println("<tr>");
            Dziecko d = przedszkole.getDziecko(dzieckoKey);
            Zuzycie zuzycie = rozliczenieMiesieczne.getZuzycie();
            response.println("<td>" + d.getImieNazwisko() + "</td>");
            response.println("<td>" + (d.getPesel() != null ? d.getPesel() : "") + "</td>");
            response.println("<td>" + (d.getGrupa() != null ? d.getGrupa().getCategory() : "") + "</td>");
            response.println("<td>" + StringToolbox.d2s(zuzycie.getDniZywienie(d.getKey().getId())) + "</td>");
            response.println("<td>" + StringToolbox.d2s(zuzycie.getZywienie(d.getKey().getId())) + "</td>");
            response.println("<td><input type=\"text\" name=\"Z_" + d.getKey().getId() + "\" value=\"" + zuzycie.getDniZywienie(d.getKey().getId()) + "\"></td>");
            response.println("</tr>");
        }

        response.println("</table><input type=\"submit\" value=\"Zapisz zmiany w żywieniu\">");
        response.println("</form>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();

        response.println("<h1>Nowa ilość dni żywienia w okresie od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()) + "</h1>");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key k : listaDzieci) {
            try {
                int v = pi(request.getParameter("Z_" + k.getId()));
                rozliczenieMiesieczne.getZuzycie().setDniZywienie(k.getId(), v);
                double vv = rozliczenieMiesieczne.getParametry().getStawkaZywieniowa();
                rozliczenieMiesieczne.getZuzycie().setZywienie(k.getId(), vv * v);
            } catch (Exception e) {
            }
        }
        IC.INSTANCE.replace(rozliczenieMiesieczne);

        response.println("<table>");
        response.println("<thead><tr>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>Dni żywienia</td>");
        response.println("<td>Żywienie</td>");
        response.println("</tr></thead>");

        int zd = 0;
        double zk = 0.0;
        for (Key k : listaDzieci) {
            response.println("<tr>");
            Dziecko d = przedszkole.getDziecko(k);
            response.println("<td>", d.getImieNazwisko(), "</td>");
            response.println("<td>", d.getPesel() != null ? d.getPesel() : "", "</td>");
            response.println("<td>", d.getGrupa() != null ? d.getGrupa().getCategory() : "", "</td>");
            int v = rozliczenieMiesieczne.getZuzycie().getDniZywienie(k.getId());
            zd += v;
            response.println("<td>" + v + "</td>");
            double vv = rozliczenieMiesieczne.getZuzycie().getZywienie(k.getId());
            zk += vv;
            response.println("<td>", StringToolbox.d2h(vv), "</td>");
            response.println("</tr>");
        }

        response.println("<tr>");
        response.println("<td>RAZEM:</td>");
        response.println("<td></td>");
        response.println("<td></td>");
        response.println("<td>" + zd + "</td>");
        response.println("<td>", StringToolbox.d2h(zk), "</td>");
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
        return null;
    }

    @Override
    protected XLSType getXLSType() {
        return null;
    }
}
