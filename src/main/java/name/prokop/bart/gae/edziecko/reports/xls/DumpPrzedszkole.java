/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import jxl.write.Label;
import jxl.write.WritableSheet;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;

/**
 *
 * @author Bartłomiej P. Prokop
 */
class DumpPrzedszkole extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        WritableSheet sheet = writableWorkbook.createSheet("Zrzut danych", 0);
        sheet.addCell(new Label(0, 0, "LP"));
        sheet.addCell(new Label(1, 0, "Klucz"));
        sheet.addCell(new Label(2, 0, "Imię i nazwisko"));
        sheet.addCell(new Label(3, 0, "PESEL"));
        sheet.addCell(new Label(4, 0, "Grupa"));
        sheet.addCell(new Label(5, 0, "Aktywne"));
        sheet.addCell(new Label(6, 0, "lp"));
        sheet.addCell(new Label(7, 0, "Klucz"));
        sheet.addCell(new Label(8, 0, "Numer karty"));
        sheet.addCell(new Label(9, 0, "Numer seryjny"));
        sheet.addCell(new Label(10, 0, "Aktywna"));
        sheet.addCell(new Label(11, 0, "PIN"));

        Przedszkole przedszkole = request.getPrzedszkole();
        int c1 = 0, c2, row = 1;
        for (Dziecko d : przedszkole.getDzieci()) {
            sheet.addCell(new Label(0, row, (++c1) + ""));
            sheet.addCell(new Label(1, row, d.getKey().getId() + ""));
            sheet.addCell(new Label(2, row, d.getImieNazwiskoAsString()));
            sheet.addCell(new Label(3, row, d.getPeselAsString()));
            sheet.addCell(new Label(4, row, d.getGrupaAsString()));
            sheet.addCell(new Label(5, row, d.isAktywne() ? "Tak" : "Nie"));
            c2 = 0;
            for (Karta k : d.getKarty()) {
                sheet.addCell(new Label(6, row, ((char) ('a' + c2++)) + ""));
                sheet.addCell(new Label(7, row, k.getKey().getId() + ""));
                sheet.addCell(new Label(8, row, k.getNumerKarty()));
                sheet.addCell(new Label(9, row, k.getNumerSeryjny()));
                sheet.addCell(new Label(10, row, (k.isAktywna() ? "Tak" : "Nie")));
                sheet.addCell(new Label(11, row, d.getPinAsString()));
                row++;
            }
            if (d.getKarty().isEmpty()) {
                row++;
            }
        }
    }
}
