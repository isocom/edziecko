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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;

public class UsunWplate extends HttpServlet {

    private static final long serialVersionUID = -7348853220460480986L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);
        RozliczenieMiesieczne rozliczenieMiesieczne = eDzieckoRequest.retrieveRozliczenieMiesieczne();

        if (request.getParameter("indexWplaty") != null) {
            int indexWplaty = Integer.parseInt(request.getParameter("indexWplaty"));
            rozliczenieMiesieczne.getWplaty().getPlatnosci().remove(indexWplaty);
            IC.INSTANCE.replace(rozliczenieMiesieczne);
        }
        response.sendRedirect("Rozliczenie?rokMiesiac=" + rozliczenieMiesieczne.getRokMiesiac());
    }
}
