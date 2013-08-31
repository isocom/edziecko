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

import com.pdfjet.*;
import java.util.ArrayList;
import java.util.List;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KartaPobytuDziecka;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 *
 * @author prokob01
 */
public class Raporty extends PDFReport {

    @Override
    protected void renderContent() throws Exception {
        zbiorowka();
        for (KartaPobytuDziecka period : request.getKidsReport().getHumanReportsSorted1()) {
            kartaPobytuDziecka(period);
        }
    }

    private void zbiorowka() throws Exception {
        newPage();
        drawHeader();

        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Zbiorcze zestawienie pobytu dzieci za okres");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);
        text.setText("od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()));
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableData1(), 1);
        table.setLineWidth(0.2);
        table.setBottomMargin(50.0);
        table.autoAdjustColumnWidths();
        table.rightAlignNumbers();
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

    private List<List<Cell>> prepareTableData1() {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();

        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Dziecko"));
        cells.add(new Cell(fontHelvetica10, "PESEL"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "Ilość dni  "));
        cells.add(new Cell(fontHelvetica10, "Koszt"));
        cells.add(new Cell(fontHelvetica10, "Obecność w dniach"));
        rows.add(cells);
        for (KartaPobytuDziecka kpd : request.getKidsReport().getHumanReportsSorted1()) {
            cells = new ArrayList<Cell>();
            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getPeselAsString()));
            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getGrupaAsString()));
            cells.add(new Cell(fontHelvetica10, kpd.getNoOfDays() + ""));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(kpd.getCalkowitaCenaPobytu())).setTextAlignment(Align.RIGHT));
            cells.add(new Cell(fontHelvetica10, kpd.getDniPobytu()));
            rows.add(cells);
        }
        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Razem"));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, request.getKidsReport().getIloscDniowek() + ""));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(request.getKidsReport().getNaleznosc())).setTextAlignment(Align.RIGHT));
        cells.add(new Cell(fontHelvetica10, ""));
        rows.add(cells);
        return rows;
    }

    private void kartaPobytuDziecka(KartaPobytuDziecka period) throws Exception {
        newPage();
        drawHeader();

        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Karta pobytu dziecka dla: " + period.getHuman().getImieNazwiskoAsString());
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);
        text.setText("od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()));
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableData2(period), 1);
        table.setLineWidth(0.2);
        table.autoAdjustColumnWidths();
        table.rightAlignNumbers();
        table.setPosition(x_pos, y_pos += 20);
        Point drawOn = table.drawOn(page);
        x_pos = drawOn.getX();
        y_pos = drawOn.getY();
        text.setText("Ilość dni: " + period.getNoOfDays());
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);
        drawFooter();
    }

    private List<List<Cell>> prepareTableData2(KartaPobytuDziecka period) {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();

        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Data"));
        cells.add(new Cell(fontHelvetica10, "Pobyt od"));
        cells.add(new Cell(fontHelvetica10, "Pobyt do"));
        cells.add(new Cell(fontHelvetica10, "Czas pobytu"));
        cells.add(new Cell(fontHelvetica10, "Koszt"));
        for (String h : period.getColumnNames()) {
            cells.add(new Cell(fontHelvetica10, h));
        }
        cells.add(new Cell(fontHelvetica10, "Uwagi"));
        rows.add(cells);

        for (DzienPobytuDziecka day : period.getDays()) {
            cells = new ArrayList<Cell>();
            cells.add(new Cell(fontHelvetica10, DateToolbox.getFormatedDate("yyyy-MM-dd", day.getData())));
            cells.add(new Cell(fontHelvetica10, DateToolbox.getFormatedDate("HH:mm:ss", day.getCzasOd())));
            cells.add(new Cell(fontHelvetica10, DateToolbox.getFormatedDate("HH:mm:ss", day.getCzasDo())));
            cells.add(new Cell(fontHelvetica10, DateToolbox.seconds2String(day.getCzasPobytu())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(day.getCenaPobytu())).setTextAlignment(Align.RIGHT));
            for (String key : day.columnNames()) {
                cells.add(new Cell(fontHelvetica10, day.columnAsString(key)));
            }
            cells.add(new Cell(fontHelvetica10, day.getUwagi()));
            rows.add(cells);
        }

        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Razem"));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(period.getCalkowitaCenaPobytu())));
        for (String h : period.getColumnNames()) {
            cells.add(new Cell(fontHelvetica10, period.sumColumn(h) + ""));
        }
        cells.add(new Cell(fontHelvetica10, ""));
        rows.add(cells);
        return rows;
    }
}
