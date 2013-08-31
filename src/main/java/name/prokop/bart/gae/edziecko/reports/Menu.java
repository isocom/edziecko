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

import java.util.Date;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class Menu extends EDzieckoServlet {

    private static final long serialVersionUID = -1245667592036050108L;

    /**
     * 
     * @param response
     * @param label
     * @param param 
     */
    private void doLink(EDzieckoResponse response, String label, String param) {
        response.print("<tr><td>");
        response.print("<a href=\"");
        response.print("Raporty?rokMiesiac=" + param);
        response.print("\">");
        response.println("Raport (czas) pobytu dzieci " + label + "</a></td><td>");
        response.print("<a href=\"");
        response.print("Rozliczenie?rokMiesiac=" + param);
        response.print("\">");
        response.println("Rozliczenie (finansowe)" + label + "</a></td></tr>");
    }

    private void license(EDzieckoRequest request, EDzieckoResponse response) {
        Date terminLicencji = request.getPrzedszkole().getTerminLicencji();
        if (terminLicencji.before(new Date())) {
            response.println("<h1>Uwaga licencja wygasła w dniu: " + DateToolbox.getFormatedDate("yyyy-MM-dd", terminLicencji) + "</h1>");
            response.println("<h1>Prosimy o kontakt z firmą ISOCOM celem przedłużenia licencji na dostęp do systemu e-Dziecko</h1>");
        }
    }

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        license(request, response);
        if (request.isAdmin()) {
            response.println("<h2>Narzędzia administracyjne</h2>");
            response.println("<a href=\"UstawKluczPrzedszkola\">Ustaw klucz przedszkola</a><br>");
            response.println("<a href=\"DodajDziecko\">Dodaj nowe dziecko</a><br>");
            response.println("<a href=\"DodajKarte\">Dodaj karte dodatkową</a><br>");
        } else {
            response.println("<h2>Informacja</h2>");
            response.println("<pre>" + HTMLRaport.getStringFromFile("info.txt") + "</pre>");
        }
        response.println("<h2>Przedszkole</h2>");
        response.println("<pre>" + request.getPrzedszkole() + "</pre>");
        response.println("<h2>Administracja</h2>");
        response.println("<a href=\"" + request.getLogoutUrl() + "\">Wyloguj " + request.getUserPrincipal().getName() + " z bieżącego przedszkola</a><br>");
        response.println("<a href=\"DumpPrzedszkole\">Lista dzieci i kart</a>, ");
        response.println("<a href=\"EdytujDzieci\">Edycja parametrów dzieci</a><br>");
        response.println("<a href=\"DodajZdarzenie\">Dodaj Zdarzenie</a>, ");
        response.println("<a href=\"Rejestrator\">Obsługa rejestratora</a><br>");

        response.println("<h1>Dostępne okresy</h1>");
        response.println("<table>");
        doLink(response, "za wrzesień 2011", "201109");
        doLink(response, "za październik 2011", "201110");
        doLink(response, "za listopad 2011", "201111");
        doLink(response, "za grudzień 2011", "201112");
        doLink(response, "za styczeń 2012", "201201");
        doLink(response, "za luty 2012", "201202");
        doLink(response, "za marzec 2012", "201203");
        doLink(response, "za kwiecień 2012", "201204");
        doLink(response, "za maj 2012", "201205");
        doLink(response, "za czerwiec 2012", "201206");
        doLink(response, "za lipiec 2012", "201207");
        doLink(response, "za sierpień 2012", "201208");
        doLink(response, "za wrzesień 2012", "201209");
        doLink(response, "za październik 2012", "201210");
        doLink(response, "za listopad 2012", "201211");
        doLink(response, "za grudzień 2012", "201212");
        doLink(response, "za styczeń 2013", "201301");
        doLink(response, "za luty 2013", "201302");
        doLink(response, "za marzec 2013", "201303");
        doLink(response, "za kwiecień 2013", "201304");
        doLink(response, "za maj 2013", "201305");
        doLink(response, "za czerwiec 2013", "201306");
        doLink(response, "za lipiec 2013", "201307");
        doLink(response, "za sierpień 2013", "201308");
        doLink(response, "za wrzesień 2013", "201309");
        doLink(response, "za październik 2013", "201310");
        doLink(response, "za listopad 2013", "201311");
        doLink(response, "za grudzień 2013", "201312");
        response.println("</table>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getTitle() {
        return "Menu główne";
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
