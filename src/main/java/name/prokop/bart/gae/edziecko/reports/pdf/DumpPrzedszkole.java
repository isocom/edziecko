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

import com.pdfjet.Cell;
import com.pdfjet.Table;
import com.pdfjet.TextLine;
import java.util.ArrayList;
import java.util.List;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;

/**
 *
 * @author prokob01
 */
public class DumpPrzedszkole extends PDFReport {

    DumpPrzedszkole() {
    }

    @Override
    protected void renderContent() throws Exception {
        newPageA4();
        drawHeader();
        TextLine text = new TextLine(fontTimesRoman14);

        text.setText("Dane o dzieciach dla potrzeb rozliczenia należności za pobyt.");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableData(), 2);

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
        cells.add(new Cell(fontHelvetica10, "LP"));
        cells.add(new Cell(fontHelvetica10, "Klucz"));
        cells.add(new Cell(fontHelvetica10, "Imię i nazwisko"));
        cells.add(new Cell(fontHelvetica10, "PESEL"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "Aktywne"));
        rows.add(cells);
        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "lp"));
        cells.add(new Cell(fontHelvetica10, "Klucz"));
        cells.add(new Cell(fontHelvetica10, "Numer karty"));
        cells.add(new Cell(fontHelvetica10, "Numer seryjny"));
        cells.add(new Cell(fontHelvetica10, "Aktywna"));
        cells.add(new Cell(fontHelvetica10, "PIN"));
        rows.add(cells);

        Przedszkole przedszkole = request.getPrzedszkole();
        int c1 = 0, c2;
        for (Dziecko d : przedszkole.getDzieci()) {
            cells = new ArrayList<Cell>();
            cells.add(new Cell(fontHelvetica6, (++c1) + ""));
            cells.add(new Cell(fontHelvetica6, d.getKey().getId() + ""));
            cells.add(new Cell(fontHelvetica6, d.getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica6, d.getPeselAsString()));
            cells.add(new Cell(fontHelvetica6, d.getGrupaAsString()));
            cells.add(new Cell(fontHelvetica6, d.isAktywne() ? "Tak" : "Nie"));
            rows.add(cells);
            c2 = 0;
            for (Karta k : d.getKarty()) {
                cells = new ArrayList<Cell>();
                cells.add(new Cell(fontHelvetica6, ((char) ('a' + c2++)) + ""));
                cells.add(new Cell(fontHelvetica6, k.getKey().getId() + ""));
                cells.add(new Cell(fontHelvetica6, k.getNumerKarty()));
                cells.add(new Cell(fontHelvetica6, k.getNumerSeryjny()));
                cells.add(new Cell(fontHelvetica6, (k.isAktywna() ? "Tak" : "Nie")));
                cells.add(new Cell(fontHelvetica6, d.getPinAsString()));
                rows.add(cells);
            }
        }
        return rows;
    }
}
