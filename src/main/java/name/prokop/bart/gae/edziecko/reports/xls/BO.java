/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import com.google.appengine.api.datastore.Key;
import java.util.Set;
import jxl.write.Label;
import jxl.write.WritableSheet;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

/**
 *
 * @author Bartłomiej P. Prokop
 */
class BO extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        sheet(0, false);
        sheet(1, true);
    }

    private void sheet(int idx, boolean onlyActive) throws Exception {
        String title = "BO  od " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom()) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo());
        if (onlyActive) {
            title = "AKT " + title;
        } else {
            title = "ALL " + title;
        }
        WritableSheet sheet = writableWorkbook.createSheet(title, idx);

        sheet.addCell(new Label(0, 0, "Dziecko"));
        sheet.addCell(new Label(1, 0, "Pesel"));
        sheet.addCell(new Label(2, 0, "Grupa"));
        sheet.addCell(new Label(3, 0, "BO Opieka"));
        sheet.addCell(new Label(4, 0, "Nadpłata"));
        sheet.addCell(new Label(5, 0, "Zaległość"));
        sheet.addCell(new Label(6, 0, "BO Żywienie"));
        sheet.addCell(new Label(7, 0, "Nadpłata"));
        sheet.addCell(new Label(8, 0, "Zaległość"));


        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        int row = 1;
        for (Key dzieckoKey : listaDzieci) {
            Dziecko d = request.getPrzedszkole().getDziecko(dzieckoKey);
            if (onlyActive && !d.isAktywne()) {
                continue;
            }
            long dk = d.getKey().getId();
            sheet.addCell(new Label(0, row, d.getImieNazwiskoAsString()));
            sheet.addCell(new Label(1, row, d.getPeselAsString()));
            sheet.addCell(new Label(2, row, d.getGrupaAsString()));
            sheet.addCell(new jxl.write.Number(3, row, rozliczenieMiesieczne.getBilansOtwarcia().getOpieka(dk)));
            sheet.addCell(new jxl.write.Number(4, row, rozliczenieMiesieczne.getBilansOtwarcia().getOpiekaNadplata(dk)));
            sheet.addCell(new jxl.write.Number(5, row, rozliczenieMiesieczne.getBilansOtwarcia().getOpiekaZaleglosc(dk)));
            sheet.addCell(new jxl.write.Number(6, row, rozliczenieMiesieczne.getBilansOtwarcia().getZywienie(dk)));
            sheet.addCell(new jxl.write.Number(7, row, rozliczenieMiesieczne.getBilansOtwarcia().getZywienieNadplata(dk)));
            sheet.addCell(new jxl.write.Number(8, row, rozliczenieMiesieczne.getBilansOtwarcia().getZywienieZaleglosc(dk)));
            row++;
        }

        if (onlyActive) {
            return;
        }
        sheet.addCell(new Label(0, row, "Razem"));
        sheet.addCell(new jxl.write.Number(3, row, rozliczenieMiesieczne.getBilansOtwarcia().sumOpieka()));
        sheet.addCell(new jxl.write.Number(4, row, rozliczenieMiesieczne.getBilansOtwarcia().sumNadplataOpieka()));
        sheet.addCell(new jxl.write.Number(5, row, rozliczenieMiesieczne.getBilansOtwarcia().sumZalegloscOpieka()));
        sheet.addCell(new jxl.write.Number(6, row, rozliczenieMiesieczne.getBilansOtwarcia().sumZywienie()));
        sheet.addCell(new jxl.write.Number(7, row, rozliczenieMiesieczne.getBilansOtwarcia().sumNadplataZywienie()));
        sheet.addCell(new jxl.write.Number(8, row, rozliczenieMiesieczne.getBilansOtwarcia().sumZalegloscZywienie()));
    }
}
