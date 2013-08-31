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
package name.prokop.bart.gae.edziecko.reports.pdf;

import com.pdfjet.*;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.VersionInfo;

public abstract class PDFReport {

    protected EDzieckoRequest request;
    protected PDF pdf;
    protected Page page;
    protected Font fontTimesRoman14;
    protected Font fontHelvetica14;
    protected Font fontTimesRoman10;
    protected Font fontHelvetica10;
    protected Font fontHelvetica6;
    protected double x_pos;
    protected double y_pos;
    private int pageNumber;

    private void initialize() throws Exception {
        fontTimesRoman14 = new Font(pdf, CoreFont.TIMES_ROMAN);
        fontTimesRoman14.setSize(14);
        fontHelvetica14 = new Font(pdf, CoreFont.HELVETICA);
        fontHelvetica14.setSize(14);
        fontTimesRoman10 = new Font(pdf, CoreFont.TIMES_ROMAN);
        fontTimesRoman10.setSize(10);
        fontHelvetica10 = new Font(pdf, CoreFont.HELVETICA);
        fontHelvetica10.setSize(10);
        fontHelvetica6 = new Font(pdf, CoreFont.HELVETICA);
        fontHelvetica6.setSize(6);
        pageNumber = 0;
    }

    protected void newPage() throws Exception {
        page = new Page(pdf, A4.PORTRAIT);
        x_pos = 20.0;
        y_pos = 20.0;
        pageNumber++;
    }

    protected void newPageLandscape() throws Exception {
        page = new Page(pdf, A4.LANDSCAPE);
        x_pos = 20.0;
        y_pos = 20.0;
        pageNumber++;
    }

    protected abstract void renderContent() throws Exception;

    final byte[] producePDF(EDzieckoRequest request) throws Exception {
        this.request = request;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        pdf = new PDF(fos);

        initialize();
        renderContent();

        pdf.flush();
        fos.close();
        return fos.toByteArray();
    }

    protected void drawHeader() throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();

        TextLine text = new TextLine(fontHelvetica14);
        text.setText(przedszkole.getNazwa() + ", " + przedszkole.getAdres().getAddress() + ". NIP: " + przedszkole.getNip());
        text.setPosition(x_pos, y_pos += 24);
        text.drawOn(page);

        y_pos += 10;
        Line line = new Line(x_pos, y_pos, page.getWidth() - x_pos, y_pos);
        line.drawOn(page);
    }

    protected void drawFooter() throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();

        y_pos = page.getHeight() - 40;
        Line line = new Line(x_pos, y_pos, page.getWidth() - x_pos, y_pos);
        line.drawOn(page);

        TextLine text = new TextLine(fontHelvetica6);
        text.setText("Klucz GAE: " + przedszkole.getKey().getId() + ". Wygenerowano: " + new Date() + " z systemu eDziecko wersja " + VersionInfo.getVersionInfo() + ". Copyright © 2012 BPP. Więcej informacji na http://edziecko.isocom.eu/ Strona nr " + pageNumber);
        text.setPosition(x_pos, y_pos += 10);
        text.drawOn(page);
    }
}
