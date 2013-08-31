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
package name.prokop.bart.gae.edziecko.ola;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Uzytkownik;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;

/**
 * Klasa do ustawiania numeru seryjnego. Na podstawie numeru karty przypisywany
 * jest numer seryjny
 *
 * @author bart
 *
 */
public class UstawKluczPrzedszkola extends HttpServlet {

    private static final long serialVersionUID = -5306011157615346988L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html><body>");
            out.println("<h2>Wprowadź klucz przedszkola w którym chcesz pracować</h2>");
            out.println("<form action=\"UstawKluczPrzedszkola\" method=\"post\">");
            Principal userPrincipal = request.getUserPrincipal();
            Key przedszkoleKey = Uzytkownik.findPrzedszkoleKey(userPrincipal.getName());
            long id = 0L;
            if (przedszkoleKey != null) {
                id = przedszkoleKey.getId();
            }
            out.println("Numer przedszkola:");
            out.println("<select name=\"przedszkoleId\" size=\"1\">");
            out.println("<option value=\"28001\">__ISOCOM__</option>");
            out.println("<option value=\"2023757\">Biskupiec</option>");
            out.println("<option value=\"29001\">Bratkowice</option>");
            out.println("<option value=\"2252466\">Dąbrowa</option>");
            out.println("<option value=\"5467001\">Lubenia</option>");
            out.println("<option value=\"1\">Mrowla</option>");
            out.println("<option value=\"242603\">RZ PP 02</option>");
            out.println("<option value=\"109001\">RZ PP 04</option>");
            out.println("<option value=\"124761\">RZ PP 05</option>");
            out.println("<option value=\"331279\">RZ PP 06</option>");
            out.println("<option value=\"131245\">RZ PP 07</option>");
            out.println("<option value=\"326359\">RZ PP 08</option>");
            out.println("<option value=\"332902\">RZ PP 12</option>");
            out.println("<option value=\"332218\">RZ PP 13</option>");
            out.println("<option value=\"259046\">RZ PP 14</option>");
            out.println("<option value=\"153403\">RZ PP 17</option>");
            out.println("<option value=\"126763\">RZ PP 18</option>");
            out.println("<option value=\"132354\">RZ PP 21</option>");
            out.println("<option value=\"133995\">RZ PP 22</option>");
            out.println("<option value=\"170019\">RZ PP 23</option>");
            out.println("<option value=\"115142\">RZ PP 28</option>");
            out.println("<option value=\"206525\">RZ PP 29</option>");
            out.println("<option value=\"104159\">RZ PP 34</option>");
            out.println("<option value=\"157009\">RZ PP 36</option>");
            out.println("<option value=\"108001\">RZ PP 37</option>");
            out.println("<option value=\"6217082\">RZ PP 39</option>");
            out.println("<option value=\"126413\">RZ PP 40</option>");
            out.println("<option value=\"191593\">RZ PP 38</option>");
            out.println("<option value=\"202156\">RZ PP 42</option>");
            out.println("<option value=\"202833\">RZ PP 43</option>");
            out.println("<option value=\"299582\">RZ PP 20</option>");
            out.println("<option value=\"1730022\">RZ PP 41</option>");
            out.println("<option value=\"2213250\">Rudna Wielka</option>");
            out.println("<option value=\"27146\">Świlcza</option>");
            out.println("<option value=\"20001\">Trzciana</option>");
            out.println("</select>");
            out.println("<input type=\"submit\" value=\"Zaloguj\">");
            out.println("</form><hr>");
            out.print("</body></html>");
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String przedszkoleId = request.getParameter("przedszkoleId").trim();
        Key przedszkoleKey = KeyFactory.createKey("Przedszkole", Long.valueOf(przedszkoleId));
        request.getSession().setAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID, przedszkoleKey);
        response.sendRedirect("Menu");
    }
}
