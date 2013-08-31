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
package name.prokop.bart.gae.edziecko.reports;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class Transferuj extends HttpServlet {

    private static final long serialVersionUID = -4725597153565383143L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);
        RozliczenieMiesieczne rozliczenieMiesieczne = eDzieckoRequest.retrieveRozliczenieMiesieczne();

        KidsReport report = eDzieckoRequest.getKidsReport();

        out.println("<h2>Przenoszenie kosztów do rozrachunków</h2>");
        out.println("<table border=1 cellspacing=0 cellpadding=1>");
        out.println("<thead><tr>");
        out.println("<td>Dziecko</td>");
        out.println("<td>Pesel</td>");
        out.println("<td>Grupa</td>");
        out.println("<td>Dni opieki</td>");
        out.println("<td>Koszt opieki</td>");
        out.println("<td>Dni żywienia</td>");
        out.println("<td>Koszt żywienia</td>");
        out.println("</tr></thead>");
        for (KartaPobytuDziecka kpd : report.getHumanReportsSorted1()) {
            long key = kpd.getHuman().getKey().getId();
            out.println("<tr><td>" + kpd.getHuman().getImieNazwisko() + "</td>");
            out.println("<td>" + (kpd.getHuman().getPesel() != null ? kpd.getHuman().getPesel() : "") + "</td>");
            out.println("<td>" + (kpd.getHuman().getGrupa() != null ? kpd.getHuman().getGrupa().getCategory() : "") + "</td>");

            int i = kpd.getNoOfDays();
            out.println("<td>" + i + "</td>");
            rozliczenieMiesieczne.getZuzycie().setDniOpieka(key, i);

            double d = kpd.sumDoubles("Opieka");
            d = BPMath.round(BPMath.roundCurrency(d), kpd.getRoundFactorOpieka());
            out.println("<td>" + StringToolbox.d2c(d) + "</td>");
            rozliczenieMiesieczne.getZuzycie().setOpieka(key, d);

            i = kpd.getNoOfDays();
            out.println("<td>" + i + "</td>");
            rozliczenieMiesieczne.getZuzycie().setDniZywienie(key, i);

            d = kpd.sumDoubles("Żywienie");
            d = BPMath.round(BPMath.roundCurrency(d), kpd.getRoundFactorZywienie());
            out.println("<td>" + StringToolbox.d2c(d) + "</td>");
            rozliczenieMiesieczne.getZuzycie().setZywienie(key, d);
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("Kwota całkowita: <b>" + StringToolbox.d2c(report.getNaleznosc()) + "</b>, ");
        out.println("ilość dniówek: <b>" + report.getIloscDniowek() + "</b>.<hr>");

        IC.INSTANCE.replace(rozliczenieMiesieczne);
    }
}