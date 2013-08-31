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
import name.prokop.bart.gae.edziecko.bol.Przedszkole;

public class Dzieci extends PDFReport {
    
    Dzieci() {
    }
    
    @Override
    protected void renderContent() throws Exception {
        newPage();
        drawHeader();
        TextLine text = new TextLine(fontTimesRoman14);
        
        text.setText("Dane o dzieciach dla potrzeb rozliczenia należności za pobyt.");
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
            newPage();
            drawHeader();
        }
        drawFooter();
    }
    
    private List<List<Cell>> prepareTableData() {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();
        Przedszkole przedszkole = request.getPrzedszkole();
        
        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Imię i Nazwisko"));
        cells.add(new Cell(fontHelvetica10, "PESEL"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "PIN"));
        cells.add(new Cell(fontHelvetica10, "Rabat 1"));
        cells.add(new Cell(fontHelvetica10, "Rabat 2"));
        rows.add(cells);
        
        for (Dziecko d : przedszkole.getDzieci()) {
            cells = new ArrayList<Cell>();
            cells.add(new Cell(fontHelvetica6, d.getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica6, d.getPeselAsString()));
            cells.add(new Cell(fontHelvetica6, d.getGrupaAsString()));
            cells.add(new Cell(fontHelvetica6, d.getPinAsString()));
            cells.add(new Cell(fontHelvetica6, d.getRabat1AsString()));
            cells.add(new Cell(fontHelvetica6, d.getRabat2AsString()));
            rows.add(cells);
        }
        return rows;
    }
}
