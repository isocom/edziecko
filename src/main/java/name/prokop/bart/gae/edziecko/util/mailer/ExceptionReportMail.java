/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util.mailer;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 *
 * @author prokob01
 */
public class ExceptionReportMail extends Mail {

    public ExceptionReportMail(Throwable exception) {
        this(exception, null);
    }

    public ExceptionReportMail(Throwable exception, String info) {
        addTo(new MailAddress("BPP", "prokop.bart@gmail.com"));
        addTo(new MailAddress("E-Dziecko", "edziecko@isocom.eu"));
        setSubject(exception.getClass().toString() + ": " + exception.getMessage());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        printWriter.println("Raised exception:");
        exception.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        String exStr = new String(byteArrayOutputStream.toByteArray());
        if (info != null) {
            setTextBody(info + '\n' + exStr);
        } else {
            setTextBody(exStr);
        }

    }
}
