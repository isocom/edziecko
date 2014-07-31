/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import jxl.write.WritableSheet;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class Raporty extends XLSReport {

    @Override
    protected void renderContent() throws Exception {
        WritableSheet sheet = writableWorkbook.createSheet("Zbiorowka", 0);
        // There is no XLS of Obecności.
    }
}
