/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.reports.xls;

import java.io.ByteArrayOutputStream;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public abstract class XLSReport {

    protected EDzieckoRequest request;
    protected WritableWorkbook writableWorkbook;

    final byte[] produceXLS(EDzieckoRequest request) throws Exception {
        this.request = request;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writableWorkbook = Workbook.createWorkbook(baos);
        
        renderContent();

        writableWorkbook.write();
        writableWorkbook.close();
        return baos.toByteArray();
    }

    protected abstract void renderContent() throws Exception;
}
