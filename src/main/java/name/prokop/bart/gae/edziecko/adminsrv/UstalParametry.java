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

import static name.prokop.bart.gae.edziecko.util.StringToolbox.pd;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;

public class UstalParametry extends HttpServlet {

    private static final long serialVersionUID = 2918342387426999750L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);
        RozliczenieMiesieczne rozliczenieMiesieczne = eDzieckoRequest.retrieveRozliczenieMiesieczne();

        try {
            out.println("<html><body>");
            // out.println("<h1>" + przedszkole + "</h1>");
            out.println("<h2>Ustalenie parametrów rozliczeń</h2>");
            out.println("<form action=\"UstalParametry\" method=\"post\">");
            out.println("<h3>Opłata za żywienie</h3>");
            out.println("Stawka żwieniowa (wsad do kotła):<input type=\"text\" name=\"stawka\" value=\"" + rozliczenieMiesieczne.getParametry().getStawkaZywieniowa() + "\">");
            out.println("<input type=\"submit\" value=\"Zapisz Parametry\">");
            out.println("</form>");
            out.print("</body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);
        RozliczenieMiesieczne rozliczenieMiesieczne = eDzieckoRequest.retrieveRozliczenieMiesieczne();

        double stawka = pd(request.getParameter("stawka"));
        rozliczenieMiesieczne.getParametry().setStawkaZywieniowa(stawka);
        IC.INSTANCE.replace(rozliczenieMiesieczne);
        response.sendRedirect("Rozliczenie?rokMiesiac=" + rozliczenieMiesieczne.getRokMiesiac());
    }
}
