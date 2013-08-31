/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import jxl.write.WritableSheet;

/**
 *
 * @author Administrator
 */
public class Raporty extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        WritableSheet sheet = writableWorkbook.createSheet("Zbiorowka", 0);
//                cells.add(new Cell(fontHelvetica10, "Dziecko"));
//        cells.add(new Cell(fontHelvetica10, "PESEL"));
//        cells.add(new Cell(fontHelvetica10, "Grupa"));
//        cells.add(new Cell(fontHelvetica10, "Ilość dni"));
//        cells.add(new Cell(fontHelvetica10, "Koszt"));
//        cells.add(new Cell(fontHelvetica10, "Obecność w dniach"));
//        rows.add(cells);
//        for (KartaPobytuDziecka kpd : request.getKidsReport().getHumanReportsSorted1()) {
//            cells = new ArrayList<Cell>();
//            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getImieNazwiskoAsString()));
//            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getPeselAsString()));
//            cells.add(new Cell(fontHelvetica10, kpd.getHuman().getGrupaAsString()));
//            cells.add(new Cell(fontHelvetica10, kpd.getNoOfDays() + ""));
//            cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(kpd.getCalkowitaCenaPobytu())).setTextAlignment(Align.RIGHT));
//            cells.add(new Cell(fontHelvetica10, kpd.getDniPobytu()));
//            rows.add(cells);
//        }
//        cells = new ArrayList<Cell>();
//        cells.add(new Cell(fontHelvetica10, "Razem"));
//        cells.add(new Cell(fontHelvetica10, ""));
//        cells.add(new Cell(fontHelvetica10, ""));
//        cells.add(new Cell(fontHelvetica10, request.getKidsReport().getIloscDniowek() + ""));
//        cells.add(new Cell(fontHelvetica10, StringToolbox.d2c(request.getKidsReport().getNaleznosc())).setTextAlignment(Align.RIGHT));
//        cells.add(new Cell(fontHelvetica10, ""));

    }
}
