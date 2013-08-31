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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class PDFServlet extends HttpServlet {

    private static final long serialVersionUID = -5683990403175030714L;

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!EDzieckoServlet.checkCredentials(req, resp)) {
            return;
        }
        EDzieckoRequest request = new EDzieckoRequest(req);
        byte[] pdf;
        PDFType type;
        try {
            type = PDFType.valueOf(request.getParameter("type"));
            PDFReport pdfReport = type.getRenderer();
            pdf = pdfReport.producePDF(request);
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
            resp.getWriter().close();
            return;
        }
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; fileName=" + type.buildName(request));
        resp.getOutputStream().write(pdf);
        resp.getOutputStream().close();
    }
}
