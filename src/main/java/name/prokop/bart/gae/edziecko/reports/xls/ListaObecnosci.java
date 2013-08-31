/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import java.util.Date;
import jxl.write.Label;
import jxl.write.WritableSheet;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.KartaPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;

/**
 *
 * @author Administrator
 */
class ListaObecnosci extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        Date dzien = (Date) request.getSession().getAttribute("data");

        ZbiorZdarzen zbiorZdarzen = ZbiorZdarzen.getZbiorZdarzen(request.getPrzedszkoleKey(), dzien);
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        KidsReport report = new KidsReport(zbiorZdarzen, rozliczenieMiesieczne);

        WritableSheet sheet = null;
        int sheetCounter = 0;
        int row = 0;

        String category = null;
        for (KartaPobytuDziecka k : report.getHumanReportsSorted1()) {
            Dziecko h = k.getHuman();
            if (!h.getGrupaAsString().equals(category)) {
                category = h.getGrupaAsString();
                sheet = writableWorkbook.createSheet("Grupa " + category, sheetCounter++);
                sheet.addCell(new Label(0, 0, "Lp."));
                sheet.addCell(new Label(1, 0, "Imię i Nazwisko"));
                row = 0;
            }
            sheet.addCell(new Label(0, ++row, row+""));
            sheet.addCell(new Label(1, row, h.getImieNazwiskoAsString()));
        }
    }
}
