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

import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class Raporty extends EDzieckoServlet {
    
    private static final long serialVersionUID = -5142492573193650472L;
    
    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        response.println("<h1>e-Dziecko, raporty z pabytu dzieci</h1>");
        HTMLRaport.generateZbiorowka(request, response);
        response.println("<a href=\"javascript:transferuj()\">Transferuj do rozrachunków</a>");
        HTMLRaport.generateListyObecnosci(request, response);
        for (KartaPobytuDziecka period : request.getKidsReport().getHumanReportsSorted1()) {
            try {
                HTMLRaport.generateSzczegolyDziecka(response, period);
            } catch (Exception e) {
                response.println("EXCEPTION: " + period);
            }
        }
    }
    
    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }
    
    @Override
    protected String getTitle() {
        return "Raporty miesięczne";
    }
    
    @Override
    protected PDFType getPDFType() {
        return PDFType.Raporty;
    }
    
    @Override
    protected String getScript() {
        StringBuilder sb = new StringBuilder();
        sb.append("function transferuj() {");
        sb.append("  if (confirm(\"Czy potwierdzasz transfer danych do rozrachunków?\")) {");
        //sb.append("    reboot_seconds=30;");
        sb.append("    location='Transferuj';");
        sb.append("  }");
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    protected XLSType getXLSType() {
        return null;
    }
}
