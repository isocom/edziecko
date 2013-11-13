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

import com.google.appengine.api.datastore.Key;
import com.pdfjet.Cell;
import com.pdfjet.Table;
import com.pdfjet.TextLine;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 *
 * @author prokob01
 */
public class BO extends PDFReport {

    @Override
    protected void renderContent() throws Exception {
        newPageA4();
        drawHeader();
        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Bilans otwarcia");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        text = new TextLine(fontTimesRoman10);
        text.setText("Okres od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()) + ".");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableData(), 1);

        table.setLineWidth(0.2);
        table.autoAdjustColumnWidths();
        table.rightAlignNumbers();
        table.setBottomMargin(50.0);
        while (true) {
            table.setPosition(x_pos, y_pos += 20);
            table.drawOn(page);
            if (!table.hasMoreData()) {
                break;
            }
            drawFooter();
            newPageA4();
            drawHeader();
        }
        drawFooter();
    }

    private List<List<Cell>> prepareTableData() {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();

        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Dziecko"));
        cells.add(new Cell(fontHelvetica10, "Pesel"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "BO Opieka"));
        cells.add(new Cell(fontHelvetica10, "Nadpłata"));
        cells.add(new Cell(fontHelvetica10, "Zaległość"));
        cells.add(new Cell(fontHelvetica10, "BO Żywienie"));
        cells.add(new Cell(fontHelvetica10, "Nadpłata"));
        cells.add(new Cell(fontHelvetica10, "Zaległość"));
        rows.add(cells);

        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            cells = new ArrayList<Cell>();
            Dziecko d = request.getPrzedszkole().getDziecko(dzieckoKey);
            cells.add(new Cell(fontHelvetica10, d.getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica10, d.getPeselAsString()));
            cells.add(new Cell(fontHelvetica10, d.getGrupaAsString()));
            double v = rozliczenieMiesieczne.getBilansOtwarcia().getOpieka(d.getKey().getId());
            String s = StringToolbox.d2s(v);
            cells.add(new Cell(fontHelvetica10, s));
            cells.add(new Cell(fontHelvetica10, v > 0.0 ? s : ""));
            cells.add(new Cell(fontHelvetica10, v < 0.0 ? s.substring(1) : ""));
            v = rozliczenieMiesieczne.getBilansOtwarcia().getZywienie(d.getKey().getId());
            s = StringToolbox.d2s(v);
            cells.add(new Cell(fontHelvetica10, s));
            cells.add(new Cell(fontHelvetica10, v > 0.0 ? s : ""));
            cells.add(new Cell(fontHelvetica10, v < 0.0 ? s.substring(1) : ""));
            rows.add(cells);
        }

        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Razem"));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumOpieka())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumNadplataOpieka())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumZalegloscOpieka())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumZywienie())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumNadplataZywienie())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2s(rozliczenieMiesieczne.getBilansOtwarcia().sumZalegloscZywienie())));
        rows.add(cells);

        return rows;
    }
}
