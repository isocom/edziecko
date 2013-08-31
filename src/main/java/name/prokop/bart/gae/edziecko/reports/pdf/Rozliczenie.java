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
import com.google.appengine.api.datastore.KeyFactory;
import com.pdfjet.Border;
import com.pdfjet.Cell;
import com.pdfjet.Table;
import com.pdfjet.TextLine;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.*;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.d2s;

/**
 *
 * @author prokob01
 */
public class Rozliczenie extends PDFReport {

    Rozliczenie() {
    }

    @Override
    protected void renderContent() throws Exception {
        generateRozliczenie();
        generateWplaty();
    }

    private void generateRozliczenie() throws Exception {
        newPageLandscape();
        drawHeader();

        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Rozliczenie finansowe za okres");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);
        text.setText("od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()));
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableRozliczenie(), 1);
        table.setLineWidth(0.2);
        table.autoAdjustColumnWidths();
        table.rightAlignNumbers();
        while (true) {
            table.setPosition(x_pos, y_pos += 20);
            table.drawOn(page);
            if (!table.hasMoreData()) {
                break;
            }
            drawFooter();
            newPageLandscape();
            drawHeader();
        }
        drawFooter();
    }

    private void generateWplaty() throws Exception {
        newPage();
        drawHeader();

        TextLine text = new TextLine(fontTimesRoman14);
        text.setText("Zestawienie wpłat za okres");
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);
        text.setText("od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo()));
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        Table table = new Table();
        table.setData(prepareTableWplaty(), 1);
        table.setLineWidth(0.2);
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

    private List<List<Cell>> prepareTableRozliczenie() {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();

        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "Imię i Nazwisko"));
        cells.add(new Cell(fontHelvetica10, "Pesel"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "BO"));
        cells.add(new Cell(fontHelvetica10, "Nadpł."));
        cells.add(new Cell(fontHelvetica10, "Zaleg."));
        cells.add(new Cell(fontHelvetica10, "dni"));
        cells.add(new Cell(fontHelvetica10, "Opieka"));
        cells.add(new Cell(fontHelvetica10, "Wpłaty"));
        double s_o_bz = 0.0;
        cells.add(new Cell(fontHelvetica10, "BZ"));
        double s_o_zal = 0.0;
        cells.add(new Cell(fontHelvetica10, "Zalic."));
        double s_o_pay = 0.0;
        cells.add(new Cell(fontHelvetica10, "Do zapł."));

        cells.add(new Cell(fontHelvetica10, "BO"));
        cells.add(new Cell(fontHelvetica10, "Nadpłata"));
        cells.add(new Cell(fontHelvetica10, "Zaległość"));
        cells.add(new Cell(fontHelvetica10, "dni"));
        cells.add(new Cell(fontHelvetica10, "Żywienie"));
        cells.add(new Cell(fontHelvetica10, "Wpłaty"));
        double s_z_bz = 0.0;
        cells.add(new Cell(fontHelvetica10, "BZ"));
        double s_z_zal = 0.0;
        cells.add(new Cell(fontHelvetica10, "Zalicz."));
        double s_z_pay = 0.0;
        cells.add(new Cell(fontHelvetica10, "Do zapł"));
        cells.add(new Cell(fontHelvetica10, "RAZEM:"));
        rows.add(cells);

        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        BilansOtwarcia bilansOtwarcia = rozliczenieMiesieczne.getBilansOtwarcia();
        Wplaty wplaty = rozliczenieMiesieczne.getWplaty();
        Zuzycie zuzycie = rozliczenieMiesieczne.getZuzycie();
        Zaliczki zaliczki = rozliczenieMiesieczne.getZaliczki();

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            cells = new ArrayList<Cell>();
            long key = dzieckoKey.getId();
            Dziecko dziecko = przedszkole.getDziecko(dzieckoKey);
            Wplata wplata = wplaty.liczWplateSumaryczna(dzieckoKey.getId());

            cells.add(new Cell(fontHelvetica6, dziecko.getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica6, dziecko.getPeselAsString()));
            cells.add(new Cell(fontHelvetica6, dziecko.getGrupaAsString()));

            // *********** OPIEKA
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getOpieka(key))));
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getOpiekaNadplata(key))));
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getOpiekaZaleglosc(key))));
            cells.add(new Cell(fontHelvetica6, zuzycie.getDniOpieka(dzieckoKey.getId()) + ""));
            cells.add(new Cell(fontHelvetica6, d2s(zuzycie.getOpieka(dzieckoKey.getId()))));
            cells.add(new Cell(fontHelvetica6, d2s(wplata.getOpieka())));
            final double o_bz = rozliczenieMiesieczne.getSaldoOpieka(dzieckoKey.getId());
            s_o_bz += o_bz;
            cells.add(new Cell(fontHelvetica6, d2s(o_bz)));
            s_o_zal += zaliczki.getOpieka(key);
            cells.add(new Cell(fontHelvetica6, d2s(zaliczki.getOpieka(key))));
            final double o_pay = rozliczenieMiesieczne.opiekaDoZaplaty(key);
            s_o_pay += o_pay;
            cells.add(new Cell(fontHelvetica6, d2s(o_pay)));

            // *********** ZYWIENIE
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getZywienie(key))));
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getZywienieNadplata(key))));
            cells.add(new Cell(fontHelvetica6, d2s(bilansOtwarcia.getZywienieZaleglosc(key))));
            cells.add(new Cell(fontHelvetica6, zuzycie.getDniZywienie(dzieckoKey.getId()) + ""));
            cells.add(new Cell(fontHelvetica6, d2s(zuzycie.getZywienie(dzieckoKey.getId()))));
            cells.add(new Cell(fontHelvetica6, d2s(wplata.getZywienie())));
            final double z_bz = rozliczenieMiesieczne.getSaldoZywienie(dzieckoKey.getId());
            s_z_bz += z_bz;
            cells.add(new Cell(fontHelvetica6, d2s(z_bz)));
            s_z_zal += zaliczki.getZywienie(dzieckoKey.getId());
            cells.add(new Cell(fontHelvetica6, d2s(zaliczki.getZywienie(dzieckoKey.getId()))));
            final double z_pay = rozliczenieMiesieczne.zywienieDoZaplaty(key);
            s_z_pay += z_pay;
            cells.add(new Cell(fontHelvetica6, d2s(z_pay)));

            cells.add(new Cell(fontHelvetica6, d2s(o_pay + z_pay)));
            rows.add(cells);
        }

        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10, "RAZEM"));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, ""));
        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumOpieka())));
        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumNadplataOpieka())));
        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumZalegloscOpieka())));
        cells.add(new Cell(fontHelvetica10, zuzycie.sumDniOpieka() + ""));
        cells.add(new Cell(fontHelvetica10, d2s(zuzycie.sumOpieka())));
        cells.add(new Cell(fontHelvetica10, d2s(wplaty.sumOpieka())));
        cells.add(new Cell(fontHelvetica10, d2s(s_o_bz)));
        cells.add(new Cell(fontHelvetica10, d2s(s_o_zal)));
        cells.add(new Cell(fontHelvetica10, d2s(s_o_pay)));

        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumZywienie())));
        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumNadplataZywienie())));
        cells.add(new Cell(fontHelvetica10, d2s(bilansOtwarcia.sumZalegloscZywienie())));
        cells.add(new Cell(fontHelvetica10, zuzycie.sumDniZywienie() + ""));
        cells.add(new Cell(fontHelvetica10, d2s(zuzycie.sumZywienie())));
        cells.add(new Cell(fontHelvetica10, d2s(wplaty.sumZywienie())));
        cells.add(new Cell(fontHelvetica10, d2s(s_z_bz)));
        cells.add(new Cell(fontHelvetica10, d2s(s_z_zal)));
        cells.add(new Cell(fontHelvetica10, d2s(s_z_pay)));

        cells.add(new Cell(fontHelvetica10, d2s(s_o_pay + s_z_pay)));
        rows.add(cells);

        return rows;
    }

    private List<List<Cell>> prepareTableWplaty() {
        List<List<Cell>> rows = new ArrayList<List<Cell>>();

        List<Cell> cells = new ArrayList<Cell>();

        cells.add(new Cell(fontHelvetica10, "Imię i Nazwisko"));
        cells.add(new Cell(fontHelvetica10, "Pesel"));
        cells.add(new Cell(fontHelvetica10, "Grupa"));
        cells.add(new Cell(fontHelvetica10, "Data wpłaty"));
        cells.add(new Cell(fontHelvetica10, "Zmiana opieki"));
        cells.add(new Cell(fontHelvetica10, "Wpłata opieka"));
        cells.add(new Cell(fontHelvetica10, "Wypłata opieka"));
        cells.add(new Cell(fontHelvetica10, "Zmiana żywienia"));
        cells.add(new Cell(fontHelvetica10, "Zmiana żywienia"));
        cells.add(new Cell(fontHelvetica10, "Zmiana żywienia"));
        cells.add(new Cell(fontHelvetica10, "Razem"));
        rows.add(cells);

        Wplaty wplaty = request.retrieveRozliczenieMiesieczne().getWplaty();
        Date dataPoprzedniejWplaty = null;
        for (int i = 0; i < wplaty.getPlatnosci().size(); i++) {
            Wplata w = wplaty.getPlatnosci().get(i);
            if (dataPoprzedniejWplaty != null && dataPoprzedniejWplaty.before(w.getDzienZaplaty())) {
                prepareTableWplaty(rows, wplaty, dataPoprzedniejWplaty);
            }
            cells = new ArrayList<Cell>();
            Dziecko dziecko = request.getPrzedszkole().getDziecko(KeyFactory.createKey(request.retrieveRozliczenieMiesieczne().getPrzedszkoleKey(), "Dziecko", w.getDzieckoKey()));
            cells.add(new Cell(fontHelvetica10, dziecko.getImieNazwiskoAsString()));
            cells.add(new Cell(fontHelvetica10, dziecko.getPeselAsString()));
            cells.add(new Cell(fontHelvetica10, dziecko.getGrupaAsString()));
            cells.add(new Cell(fontHelvetica10, DateToolbox.getFormatedDate("yyyy-MM-dd", w.getDzienZaplaty())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getOpieka())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getOpiekaWplata())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getOpiekaWyplata())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getZywienie())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getZywienieWplata())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getZywienieWyplata())));
            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(w.getOpieka() + w.getZywienie())));
            rows.add(cells);
            if (i + 1 == wplaty.getPlatnosci().size()) {
                prepareTableWplaty(rows, wplaty, w.getDzienZaplaty());
            }
            dataPoprzedniejWplaty = w.getDzienZaplaty();
        }

        cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, true, true, false)));
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, false, true, false)));
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, false, true, false)));
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, false, true, true)));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumOpieka())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumOpiekaWplata())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumOpiekaWyplata())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumZywienie())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumZywienieWplata())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sumZywienieWyplata())));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.sum())));
        rows.add(cells);

        return rows;
    }

    private void prepareTableWplaty(List<List<Cell>> rows, Wplaty wplaty, Date date) {
        List<Cell> cells = new ArrayList<Cell>();
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, true, true, false)));
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, false, true, false)));
        cells.add(new Cell(fontHelvetica10).setBorder(new Border(true, false, true, true)));
        cells.add(new Cell(fontHelvetica10, DateToolbox.getFormatedDate("yyyy-MM-dd", date)));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaOpieka(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaOpiekaWplata(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaOpiekaWyplata(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaZywienie(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaZywienieWplata(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDziennaZywienieWyplata(date))));
        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(wplaty.liczDzienna(date))));
        rows.add(cells);
    }
}
