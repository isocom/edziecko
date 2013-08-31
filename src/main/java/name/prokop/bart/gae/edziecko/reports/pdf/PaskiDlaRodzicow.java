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
import com.pdfjet.Align;
import com.pdfjet.Cell;
import com.pdfjet.Line;
import com.pdfjet.TextLine;
import java.util.ArrayList;
import java.util.List;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KartaPobytuDziecka;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 *
 * @author prokob01
 */
public class PaskiDlaRodzicow extends PDFReport {

    private RozliczenieMiesieczne r;

    PaskiDlaRodzicow() {
    }

    @Override
    protected void renderContent() throws Exception {
        r = request.retrieveRozliczenieMiesieczne();
        newPage();
        new Line(x_pos, y_pos, page.getWidth() - x_pos, y_pos).drawOn(page);
        for (Key k : r.getListaDzieci()) {
            kartaPobytuDziecka(request.getPrzedszkole().getDziecko(k));
        }
    }

    private void kartaPobytuDziecka(Dziecko human) throws Exception {
        String s;
        long k = human.getKey().getId();
        double o, z;

        if (y_pos > page.getHeight() - 140) {
            newPage();
            new Line(x_pos, y_pos, page.getWidth() - x_pos, y_pos).drawOn(page);
        }

        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Rozliczenie bieżące dla: " + human.getImieNazwiskoAsString() + " gr. " + human.getGrupaAsString() + " PESEL: " + human.getPeselAsString());
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        text = new TextLine(fontTimesRoman10);
        text.setText("okres bieżący od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()));
        text.setPosition(x_pos, y_pos += 16);
        text.drawOn(page);

        s = "Opieka na koniec poprzedniego miesiąca: ";
        o = r.getBilansOtwarcia().getOpieka(k);
        if (o < 0) {
            s += "zaległość: " + StringToolbox.d2c(r.getBilansOtwarcia().getOpiekaZaleglosc(k));
        } else {
            s += "nadpłata: " + StringToolbox.d2c(r.getBilansOtwarcia().getOpiekaNadplata(k));
        }
        text.setText(s);
        text.setPosition(x_pos, y_pos += 12);
        text.drawOn(page);

        z = r.getBilansOtwarcia().getZywienie(k);
        s = "Żywienie na koniec poprzedniego miesiąca: ";
        if (z < 0) {
            s += "zaległość: " + StringToolbox.d2c(r.getBilansOtwarcia().getZywienieZaleglosc(k));
        } else {
            s += "nadpłata: " + StringToolbox.d2c(r.getBilansOtwarcia().getZywienieNadplata(k));
        }
        text.setText(s);
        text.setPosition(x_pos, y_pos += 12);
        text.drawOn(page);

        text.setText("Zaliczka na opiekę: " + StringToolbox.d2c(r.getZaliczki().getOpieka(k)));
        text.setPosition(x_pos, y_pos += 12);
        text.drawOn(page);
        o -= r.getZaliczki().getOpieka(k);
        text.setText("Zaliczka na zywienie: " + StringToolbox.d2c(r.getZaliczki().getZywienie(k)));
        text.setPosition(x_pos, y_pos += 12);
        text.drawOn(page);
        z -= r.getZaliczki().getZywienie(k);

        if (o < 0.0) {
            text.setText("Do zapłaty za opiekę na bieżący m-c: " + StringToolbox.d2c(-o));
            text.setPosition(x_pos, y_pos += 12);
            text.drawOn(page);
        }
        if (z < 0.0) {
            text.setText("Do zapłaty za żywienie na bieżący m-c: " + StringToolbox.d2c(-z));
            text.setPosition(x_pos, y_pos += 12);
            text.drawOn(page);
        }
        double suma = o + z;

        if (suma < 0) {
            text.setText("RAZEM do zapłaty: " + StringToolbox.d2c(-suma));
        } else {
            text.setText("Nadpłata z zaliczkami wynosi razem " + StringToolbox.d2c(suma) + ", płatność nie jest wymagana.");
        }
        text.setPosition(x_pos, y_pos += 12);
        text.drawOn(page);

        y_pos += 16;
        new Line(x_pos, y_pos, page.getWidth() - x_pos, y_pos).drawOn(page);
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
