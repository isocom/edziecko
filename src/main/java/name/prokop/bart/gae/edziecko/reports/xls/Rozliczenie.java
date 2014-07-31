/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.Set;
import jxl.write.*;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.*;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class Rozliczenie extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        rozliczenie(writableWorkbook.createSheet("AKT Rozliczenie", 0), true);
        rozliczenie(writableWorkbook.createSheet("ALL Rozliczenie", 1), false);
        wplaty(writableWorkbook.createSheet("Wpłaty", 2));
    }

    private void rozliczenie(WritableSheet sheet, boolean onlyActive) throws Exception {
        sheet.addCell(new Label(0, 0, "Dane dziecka"));
        sheet.addCell(new Label(3, 0, "Opieka"));
        sheet.addCell(new Label(12, 0, "Żywienie"));
        sheet.addCell(new Label(23, 0, "Opieka"));
        sheet.addCell(new Label(27, 0, "Żywienie"));
        sheet.mergeCells(0, 0, 2, 0);
        sheet.mergeCells(3, 0, 11, 0);
        sheet.mergeCells(12, 0, 21, 0);
        sheet.mergeCells(23, 0, 26, 0);
        sheet.mergeCells(27, 0, 30, 0);

        int c = 0, r = 1;
        WritableCellFormat integerFormat = new WritableCellFormat(NumberFormats.INTEGER);
        WritableCellFormat floatFormat = new WritableCellFormat(NumberFormats.FLOAT);

        sheet.addCell(new Label(c++, r, "Imię i Nazwisko"));
        sheet.addCell(new Label(c++, r, "Pesel"));
        sheet.addCell(new Label(c++, r, "Grupa"));
        sheet.addCell(new Label(c++, r, "BO"));
        sheet.addCell(new Label(c++, r, "Nadpł."));
        sheet.addCell(new Label(c++, r, "Zaleg."));
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Opieka"));
        sheet.addCell(new Label(c++, r, "Płatności"));
        sheet.addCell(new Label(c++, r, "BZ"));
        sheet.addCell(new Label(c++, r, "Zalic."));
        sheet.addCell(new Label(c++, r, "Do zapł."));

        sheet.addCell(new Label(c++, r, "BO"));
        sheet.addCell(new Label(c++, r, "Nadpłata"));
        sheet.addCell(new Label(c++, r, "Zaległość"));
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Żywienie"));
        sheet.addCell(new Label(c++, r, "Płatności"));
        sheet.addCell(new Label(c++, r, "BZ"));
        sheet.addCell(new Label(c++, r, "Zalicz."));
        sheet.addCell(new Label(c++, r, "Do zapł"));
        sheet.addCell(new Label(c++, r, "RAZEM:"));
        c++;
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Przypis"));
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Odpis"));
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Przypis"));
        sheet.addCell(new Label(c++, r, "dni"));
        sheet.addCell(new Label(c++, r, "Odpis"));

        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        BilansOtwarcia bilansOtwarcia = rozliczenieMiesieczne.getBilansOtwarcia();
        Wplaty wplaty = rozliczenieMiesieczne.getWplaty();
        Zuzycie zuzycie = rozliczenieMiesieczne.getZuzycie();
        Zaliczki zaliczki = rozliczenieMiesieczne.getZaliczki();

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            long key = dzieckoKey.getId();
            Dziecko dziecko = przedszkole.getDziecko(dzieckoKey);
            if (onlyActive && !dziecko.isAktywne()) {
                continue;
            }
            Wplata wplata = wplaty.liczWplateSumaryczna(key);

            c = 0;
            sheet.addCell(new Label(c++, ++r, dziecko.getImieNazwiskoAsString()));
            sheet.addCell(new Label(c++, r, dziecko.getPeselAsString()));
            sheet.addCell(new Label(c++, r, dziecko.getGrupaAsString()));

            // *********** OPIEKA
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getOpiekaNadplata(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getOpiekaZaleglosc(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, zuzycie.getDniOpieka(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, zuzycie.getOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, wplata.getOpieka(), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getSaldoOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getZaliczki().getOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.opiekaDoZaplaty(key), floatFormat));

            // *********** ZYWIENIE
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getZywienie(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getZywienieNadplata(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, bilansOtwarcia.getZywienieZaleglosc(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, zuzycie.getDniZywienie(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, zuzycie.getZywienie(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, wplata.getZywienie(), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getSaldoZywienie(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, zaliczki.getZywienie(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.zywienieDoZaplaty(key), floatFormat));

            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.opiekaDoZaplaty(key) + rozliczenieMiesieczne.zywienieDoZaplaty(key), floatFormat));
            c++;
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getDniPrzypisOpieka(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getPrzypisOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getDniOdpisOpieka(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getOdpisOpieka(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getDniPrzypisZywienie(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getPrzypisZywienie(key), floatFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getDniOdpisZywienie(key), integerFormat));
            sheet.addCell(new jxl.write.Number(c++, r, rozliczenieMiesieczne.getOdpisZywienie(key), floatFormat));
        }
    }

    private void wplaty(WritableSheet sheet) throws Exception {
        sheet.addCell(new Label(0, 0, "Imię i Nazwisko"));
        sheet.addCell(new Label(1, 0, "PESEL"));
        sheet.addCell(new Label(2, 0, "Grupa"));
        sheet.addCell(new Label(3, 0, "Data wpłaty"));
        sheet.addCell(new Label(4, 0, "Opieka"));
        sheet.addCell(new Label(5, 0, "Opieka wpłaty"));
        sheet.addCell(new Label(6, 0, "Opieka wypłaty"));
        sheet.addCell(new Label(7, 0, "Żywienie"));
        sheet.addCell(new Label(8, 0, "Żywienie wpłaty"));
        sheet.addCell(new Label(9, 0, "Żywienie wypłaty"));
        sheet.addCell(new Label(10, 0, "Razem"));

        Wplaty wplaty = request.retrieveRozliczenieMiesieczne().getWplaty();
        for (int i = 0; i < wplaty.getPlatnosci().size(); i++) {
            Wplata w = wplaty.getPlatnosci().get(i);
            Dziecko dziecko = request.getPrzedszkole().getDziecko(KeyFactory.createKey(request.retrieveRozliczenieMiesieczne().getPrzedszkoleKey(), "Dziecko", w.getDzieckoKey()));
            sheet.addCell(new Label(0, i + 1, dziecko.getImieNazwiskoAsString()));
            sheet.addCell(new Label(1, i + 1, dziecko.getPeselAsString()));
            sheet.addCell(new Label(2, i + 1, dziecko.getGrupaAsString()));
            sheet.addCell(new DateTime(3, i + 1, w.getDzienZaplaty()));
            sheet.addCell(new jxl.write.Number(4, i + 1, w.getOpieka()));
            sheet.addCell(new jxl.write.Number(5, i + 1, w.getOpiekaWplata()));
            sheet.addCell(new jxl.write.Number(6, i + 1, w.getOpiekaWyplata()));
            sheet.addCell(new jxl.write.Number(7, i + 1, w.getZywienie()));
            sheet.addCell(new jxl.write.Number(8, i + 1, w.getZywienieWplata()));
            sheet.addCell(new jxl.write.Number(9, i + 1, w.getZywienieWyplata()));
            sheet.addCell(new jxl.write.Number(10, i + 1, w.getOpieka() + w.getZywienie()));
        }
    }
}
