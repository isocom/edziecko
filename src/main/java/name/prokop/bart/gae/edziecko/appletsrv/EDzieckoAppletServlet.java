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
package name.prokop.bart.gae.edziecko.appletsrv;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.mailer.ExceptionReportMail;
import name.prokop.bart.gae.edziecko.util.mailer.Mailer;

public class EDzieckoAppletServlet extends HttpServlet {

    /**
     * Just hit fulfill
     */
    private static final long serialVersionUID = -6421575599578347530L;

    private enum RequestType {

        UploadDatabase, DownloadDatabase;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestTypeParam = request.getParameter("requestType");
        if (requestTypeParam == null) {
            response.sendError(401, "requestTypeParam == null");
            return;
        }
        RequestType requestType;
        try {
            requestType = RequestType.valueOf(requestTypeParam);
        } catch (IllegalArgumentException iae) {
            response.sendError(402, "IllegalArgumentException");
            return;
        }

        switch (requestType) {
            case UploadDatabase:
                doUploadDatabase(request, response);
                return;
            case DownloadDatabase:
                doDownloadDatabase(request, response);
                return;
        }

        response.sendError(403, "Brak case dla tej akcji");
    }

    private void doUploadDatabase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("przedszkoleId"));
        Przedszkole przedszkole = IC.INSTANCE.findPrzedszkole(id);

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(AppletDAO.storeLog(przedszkole, request.getParameter("tibboDatabase")));
        } catch (Exception e) {
            Mailer.INSTANCE.send(new ExceptionReportMail(e));
            out.println(false);
        } finally {
            out.close();
        }
    }

    private void doDownloadDatabase(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("przedszkoleId"));
        Przedszkole przedszkole = IC.INSTANCE.findPrzedszkole(id);

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(AppletDAO.produceTibboDatabase(przedszkole));
        } catch (Exception e) {
            Mailer.INSTANCE.send(new ExceptionReportMail(e));
        } finally {
            out.close();
        }
    }
}
