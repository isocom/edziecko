/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import jxl.write.Label;
import jxl.write.WritableSheet;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;

/**
 *
 * @author Administrator
 */
public class Dzieci extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        WritableSheet sheet = writableWorkbook.createSheet("Dzieci", 0);
        sheet.addCell(new Label(0, 0, "Imię i Nazwisko"));
        sheet.addCell(new Label(1, 0, "PESEL"));
        sheet.addCell(new Label(2, 0, "Grupa"));
        sheet.addCell(new Label(3, 0, "PIN"));
        sheet.addCell(new Label(4, 0, "Rabat 1"));
        sheet.addCell(new Label(5, 0, "Rabat 2"));

        Przedszkole przedszkole = request.getPrzedszkole();
        int row = 1;
        for (Dziecko d : przedszkole.getDzieci()) {
            if (!d.isAktywne()) {
                continue;
            }
            sheet.addCell(new Label(0, row, d.getImieNazwiskoAsString()));
            sheet.addCell(new Label(1, row, d.getPeselAsString()));
            sheet.addCell(new Label(2, row, d.getGrupaAsString()));
            sheet.addCell(new Label(3, row, d.getPinAsString()));
            sheet.addCell(new Label(4, row, d.getRabat1AsString()));
            sheet.addCell(new Label(5, row, d.getRabat2AsString()));
            row++;
        }
    }
}
