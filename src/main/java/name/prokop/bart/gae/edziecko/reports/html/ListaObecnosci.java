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
package name.prokop.bart.gae.edziecko.reports.html;

import java.util.Date;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.KartaPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class ListaObecnosci extends EDzieckoServlet {

    private static final long serialVersionUID = 1250533460281259519L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Date dzien = DateToolbox.parseDateChecked("yyyyMMdd", request.getParameter("dzien"));
        request.getSession().setAttribute("data", dzien);

        ZbiorZdarzen zbiorZdarzen = ZbiorZdarzen.getZbiorZdarzen(request.getPrzedszkoleKey(), dzien);
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        KidsReport report = new KidsReport(zbiorZdarzen, rozliczenieMiesieczne);

        response.println("<h1>Lista obecności w dniu: " + DateToolbox.getFormatedDate("yyyy-MM-dd", dzien) + "</h1>");

        String category = null;
        int count = 0, countTotal = 0;
        for (KartaPobytuDziecka k : report.getHumanReportsSorted1()) {
            Dziecko h = k.getHuman();
            if (!h.getGrupaAsString().equals(category)) {
                if (category != null) {
                    response.println("</ol>");
                    response.println("Liczba dzieci: " + count + ".");
                }
                response.println("<h2>Grupa: " + h.getGrupaAsString() + "</h2>");
                response.println("<ol>");
                count = 0;
                category = h.getGrupaAsString();
            }
            response.println("<li>" + h.getImieNazwisko());
            count++;
            countTotal++;
        }
        response.println("</ol>");
        response.println("Liczba dzieci: " + count + ".<br>");
        response.println("<b>Całkowita liczba dzieci: " + countTotal + ".</b>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "Lista obecności";
    }

    @Override
    protected PDFType getPDFType() {
        return PDFType.ListaObecnosci;
    }

    @Override
    protected XLSType getXLSType() {
        return XLSType.ListaObecnosci;
    }
}
