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
package name.prokop.bart.gae.edziecko.adminsrv;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class ZamowKarteDodatkowa extends EDzieckoServlet {

    private static final long serialVersionUID = 4688425982940299797L;

    private void sendMail(EDzieckoResponse response, Dziecko dziecko) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        StringBuilder msgBody = new StringBuilder();
        msgBody.append("Zamawiający:\n").append(dziecko.getPrzedszkole()).append('\n');
        msgBody.append("Dziecko:").append(dziecko);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("bart@isocom.eu", "Chmura eDziecko"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("edziecko@isocom.eu", "ISOCOM - eDziecko"));
            msg.setSubject("Testy - Zamówienie nowej karty dodatkowej");
            msg.setText(msgBody.toString());
            Transport.send(msg);
        } catch (Exception e) {
            response.println("<pre>" + e.getMessage() + "</pre>");
        }
    }

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        sendMail(response, request.getPrzedszkole().getDzieci().get(0));
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    protected PDFType getPDFType() {
        return null;
    }

    @Override
    protected XLSType getXLSType() {
        return null;
    }
}
