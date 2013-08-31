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

import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class Rejestrator extends EDzieckoServlet {

    private static final long serialVersionUID = -8366576781205779068L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        response.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html\">");
        response.println("<title>Insert title here</title></head><body>" + "Ta strona ma tylko przetestowac mozliwosc zaladowania apletu i	przekazania parametrow - serwera GAE i idPrzedszkola - do apletu<br>");
        response.println("<applet alt=\"Podpisany cyfrowo aplet EDziecko\" code=\"name.prokop.bart.hardware.bpdriver.rfid.ttd0006.v07.EDzieckoApplet.class\"	width=\"640\" height=\"480\" archive=\"applets/bart-hdw.jar,applets/bart-lib.jar\">");
        response.println("<param name=\"serverBase\" value=\"http://e-dziecko.appspot.com\">");
        response.println("<param name=\"przedszkoleKeyId\" value=\"" + request.getPrzedszkoleKey().getId() + "\">");
        response.println("</applet></body></html>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "Obsługa rejestartora";
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
