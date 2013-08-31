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
package name.prokop.bart.gae.edziecko.reports.pdf;

import com.pdfjet.TextLine;
import java.util.Date;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.KartaPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

/**
 *
 * @author prokob01
 */
public class ListaObecnosci extends PDFReport {

    @Override
    protected void renderContent() throws Exception {
        Date dzien = (Date) request.getSession().getAttribute("data");

        ZbiorZdarzen zbiorZdarzen = ZbiorZdarzen.getZbiorZdarzen(request.getPrzedszkoleKey(), dzien);
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        KidsReport report = new KidsReport(zbiorZdarzen, rozliczenieMiesieczne);

        TextLine text1 = new TextLine(fontTimesRoman14);
        TextLine text2 = new TextLine(fontHelvetica10);

        String category = null;
        for (KartaPobytuDziecka k : report.getHumanReportsSorted1()) {
            Dziecko h = k.getHuman();
            if (!h.getGrupaAsString().equals(category)) {
                if (category != null) {
                    drawFooter();
                }
                newPage();
                drawHeader();
                text1.setText("Lista obecności w dniu: " + DateToolbox.getFormatedDate("yyyy-MM-dd", dzien) + ", grupa: " + h.getGrupaAsString());
                text1.setPosition(x_pos, y_pos += 24);
                text1.drawOn(page);
                category = h.getGrupaAsString();
            }
            text2.setText(h.getImieNazwiskoAsString());
            text2.setPosition(x_pos, y_pos += 12);
            text2.drawOn(page);
        }
        drawFooter();
    }
}
