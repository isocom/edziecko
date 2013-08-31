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

import com.google.appengine.api.datastore.Key;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;

public class ExportujKarty extends HttpServlet {

    private static final long serialVersionUID = -4317928621567037183L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Przedszkole przedszkole;
            if (request.getParameter("przedszkoleId") != null) {
                Long id = Long.parseLong(request.getParameter("przedszkoleId"));
                przedszkole = IC.INSTANCE.findPrzedszkole(id);
            } else {
                Key key = (Key) request.getSession().getAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID);
                przedszkole = IC.INSTANCE.findPrzedszkole(key);
            }

            TreeMap<String, Karta> set = new TreeMap<String, Karta>();

            for (Dziecko d : przedszkole.getDzieci()) {
                for (Karta k : d.getKarty()) {
                    set.put(k.getNumerKarty(), k);
                }
            }

            int counter = 0;
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<cards>");
            for (Karta k : set.values()) {
                out.print("<card ");
                out.print("pos=\"" + (++counter) + "\" ");
                out.print("cardNumber=\"" + k.getNumerKarty() + "\" ");
                out.print("cardOwner=\"" + k.getDziecko().getImieNazwisko() + "\" ");
                out.print("serial=\"" + k.getNumerSeryjny() + "\" ");
                out.print("key=\"" + k.getKey().getId() + "\" ");
                out.print("parent=\"" + k.getDziecko().getKey().getId() + "\" ");
                out.print("/>");
                out.println();
            }
            out.println("</cards>");
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }
}
