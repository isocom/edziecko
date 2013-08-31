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
package name.prokop.bart.gae.edziecko.reports;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.BilansOtwarcia;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.d2s;

public class PobierzBZ extends EDzieckoServlet {

    private static final long serialVersionUID = -4725597153565383143L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        RozliczenieMiesieczne poprzednieRozliczenie = request.retrievePreviousRozliczenieMiesieczne();

        response.println("<h1>Przeniesienie BZ z poprzedniego miesiąca</h1>");
        response.println("<h2>Okres poprzedni od:" + DateToolbox.getFormatedDate("yyyy-MM-dd", poprzednieRozliczenie.decodeRokMiesiacFrom()) + " do:" + DateToolbox.getFormatedDate("yyyy-MM-dd", poprzednieRozliczenie.decodeRokMiesiacTo()) + "</h2>");
        response.println("<h2>Okres bieżący od:" + DateToolbox.getFormatedDate("yyyy-MM-dd", rozliczenieMiesieczne.decodeRokMiesiacFrom()) + " do:" + DateToolbox.getFormatedDate("yyyy-MM-dd", rozliczenieMiesieczne.decodeRokMiesiacTo()) + "</h2>");
        response.println("<table border=1 cellspacing=0 cellpadding=1>");
        response.println("<thead><tr>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>BZ opieki</td>");
        response.println("<td>BO opieki</td>");
        response.println("<td>BZ żywienia</td>");
        response.println("<td>BO żywienia</td>");
        response.println("</tr></thead>");

        BilansOtwarcia bilansOtwarcia = rozliczenieMiesieczne.getBilansOtwarcia();
        double sumO = 0.0, sumZ = 0.0;
        for (Dziecko d : request.getPrzedszkole().getDzieci()) {
            long key = d.getKey().getId();
            response.println("<tr><td>" + d.getImieNazwisko() + "</td>");
            response.println("<td>" + (d.getPesel() != null ? d.getPesel() : "") + "</td>");
            response.println("<td>" + (d.getGrupa() != null ? d.getGrupa().getCategory() : "") + "</td>");

            double v = poprzednieRozliczenie.getSaldoOpieka(key);
            sumO += v;
            response.println("<td>" + d2s(v) + "</td>");
            bilansOtwarcia.setOpieka(key, v);
            response.println("<td>" + d2s(bilansOtwarcia.getOpieka(key)) + "</td>");

            v = poprzednieRozliczenie.getSaldoZywienie(key);
            sumZ += v;
            response.println("<td>" + d2s(v) + "</td>");
            bilansOtwarcia.setZywienie(key, v);
            response.println("<td>" + d2s(bilansOtwarcia.getZywienie(key)) + "</td>");
            response.println("</tr>");
        }
        response.println("<tr><td>RAZEM</td>");
        response.println("<td></td>");
        response.println("<td></td>");
        response.println("<td>" + d2s(sumO) + "</td>");
        response.println("<td>" + d2s(sumO) + "</td>");
        response.println("<td>" + d2s(sumZ) + "</td>");
        response.println("<td>" + d2s(sumZ) + "</td>");
        response.println("</tr>");
        response.println("</table>");

        IC.INSTANCE.replace(rozliczenieMiesieczne);
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "Pobieranie poprzedniego miesiąca";
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