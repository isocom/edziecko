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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class MojeDziecko extends HttpServlet {

    private static final long serialVersionUID = 1914119596708215352L;
    private static final String SESSION_MD_REPORT = "MD_KIDS_REP";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            ServletOutputStream os = response.getOutputStream();
            try {
                InputStream is = getClass().getResourceAsStream("MojeDziecko.html");
                while (is.available() > 0) {
                    byte[] buffer = new byte[1024];
                    int len = is.read(buffer);
                    os.write(buffer, 0, len);
                }
            } finally {
                os.close();
            }
            return;
        }

        UUID reportId = (UUID) request.getSession().getAttribute(SESSION_MD_REPORT);
        KidsReport report = (KidsReport) IC.INSTANCE.getObject(reportId);
        if (report == null) {
            response.sendError(401);
            return;
        }

        if (action.equalsIgnoreCase("LOG")) {
            exportLOG(response, report);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cardNo = request.getParameter("cardNo");
        if (cardNo == null) {
            response.sendError(401);
            return;
        }
        cardNo = cardNo.trim();
        cardNo = StringToolbox.cardNumberPretty(StringToolbox.cardNumberCompress(cardNo));

        EDzieckoRequest eDzieckoRequest = new EDzieckoRequest(request);

        ZbiorZdarzen zbiorZdarzen;
        try {
            zbiorZdarzen = new ZbiorZdarzen(cardNo.trim(), eDzieckoRequest.getRokMiesiac());
        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println(HTMLRaport.getStartDocument("Raport dla rodzica", true));
            out.println("<div class=\"text\">");
            out.println("<h2>Raport dla rodzica</h2>");
            out.println("Niestety nie znalazłem w systemie danego numeru karty<hr>");
            e.printStackTrace(out);
            out.println("</div>");
            out.println(HTMLRaport.getEndDocument());

            out.close();
            return;
        }
        Dziecko dziecko = zbiorZdarzen.getDziecko();
        if (dziecko.getPin() == null || !dziecko.getPin().equals(request.getParameter("pin"))) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println(HTMLRaport.getStartDocument("Raport dla rodzica", true));
            out.println("<div class=\"text\">");
            out.println("<h2>Raport dla rodzica</h2>");
            out.println("Niepoprawny nr PIN, aby ustawić PIN zgłoś się do swojego przedszkola.<hr>");
            out.println("</div>");
            out.println(HTMLRaport.getEndDocument());

            out.close();
            return;
        }

        RozliczenieMiesieczne rozliczenieMiesieczne = IC.INSTANCE.retrieveRozliczenieMiesieczne(zbiorZdarzen.getPrzedszkole().getKey(), Integer.valueOf(request.getParameter("rokMiesiac")));
        KidsReport report = new KidsReport(zbiorZdarzen, rozliczenieMiesieczne);

        UUID reportId = UUID.randomUUID();
        IC.INSTANCE.addObject(reportId, report);
        request.getSession().setAttribute(SESSION_MD_REPORT, reportId);

        kidsReport(response, report);
    }

    private void kidsReport(HttpServletResponse response, KidsReport report) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(HTMLRaport.getStartDocument("Raport dla rodzica", true));
            out.println("<h2>Raport dla rodzica</h2>");

            out.println("Okres od " + DateToolbox.getFormatedDate("yyyy-MM-dd", report.getZbiorZdarzen().getFrom()) + " do ");
            out.println(DateToolbox.getFormatedDate("yyyy-MM-dd", report.getZbiorZdarzen().getTo()) + "<br>");
            out.println("Ilość zdarzeń: " + report.getZbiorZdarzen().getZdarzenia().size() + "<br>");

            out.println("Przedszkole: " + report.getZbiorZdarzen().getPrzedszkole().getNazwa() + "<br>");
            out.println("Dziecko: " + report.getZbiorZdarzen().getDziecko().getImieNazwisko() + "<br>");
            out.println("PESEL: " + report.getZbiorZdarzen().getDziecko().getPesel() + "<br>");
            if (report.getZbiorZdarzen().getDziecko().getRabat1() != null) {
                out.println("Rabat1: <b>" + report.getZbiorZdarzen().getDziecko().getRabat1() * 100 + "%</b><br>");
            }
            if (report.getZbiorZdarzen().getDziecko().getRabat2() != null) {
                out.println("Rabat2: <b>" + report.getZbiorZdarzen().getDziecko().getRabat2() * 100 + "%</b><br>");
            }

            out.println("<a href=\"/MojeDziecko?action=LOG\">Zobacz dziennik zdarzeń - rejestr przyłożeń karty</a>");
            out.println("<div>Uwaga: Niniejsze zestawienie ma wyłącznie charakter poglądowo-informacyjny.</div>");
            KartaPobytuDziecka period = report.getHumanReports().get(report.getZbiorZdarzen().getDziecko());

            if (period != null && period.getDays() != null && period.getDays().size() > 0) {
                out.println("<table border=1 class=\"tablesorter\">");
                out.println("<thead><tr>");
                out.println("<td>Data</td>");
                out.println("<td>Pobyt od</td>");
                out.println("<td>Pobyt do</td>");
                out.println("<td>Czas pobytu</td>");
                out.println("<td>Koszt</td>");
                for (String h : period.getColumnNames()) {
                    out.println("<td>" + h + "</td>");
                }
                out.println("<td>Opiekun rano</td>");
                out.println("<td>Opiekun wieczorem</td>");
                out.println("</tr></thead>");
                for (DzienPobytuDziecka day : period.getDays()) {
                    out.println("<tr>");
                    out.println("<td>" + DateToolbox.getFormatedDate("yyyy-MM-dd", day.getData()) + "</td>");
                    out.println("<td>" + DateToolbox.getFormatedDate("HH:mm:ss", day.getCzasOd()) + "</td>");
                    out.println("<td>" + DateToolbox.getFormatedDate("HH:mm:ss", day.getCzasDo()) + "</td>");
                    out.println("<td>" + DateToolbox.seconds2String(day.getCzasPobytu()) + "</td>");
                    out.println("<td>" + StringToolbox.d2c(day.getCenaPobytu()) + "</td>");
                    for (String key : day.columnNames()) {
                        out.println("<td>" + day.columnAsString(key) + "</td>");
                    }
                    out.println("<td>" + day.getOpiekunOd() + "</td>");
                    out.println("<td>" + day.getOpiekunDo() + "</td>");
                    out.println("</tr>");
                    if (day.getUwagi().length() > 0) {
                        out.println("<tr><td colspan=9><b>Uwagi: </b>" + day.getUwagi() + "</td></tr>");
                    }
                }
                out.println("</table>");
                out.print("Razem: " + StringToolbox.d2c(period.getCalkowitaCenaPobytu()) + " za ");
                out.println("ilość dni: " + period.getNoOfDays() + ".");
                out.println("<div>Podana wyżej kwota stanowi rzeczywiste wyliczenie kosztu opieki i (zwykle) kosztu żywienia w danym miesiącu, ale może nie stanowić kwoty do zapłaty. Kwota do zapłaty może dodatkowo uwzględniać zaliczki, zajęcia dodatkowe, dodatkowe żywienie, inne opłaty oraz rozliczenie miesięcy poprzednich.</div>");
            }
            out.println("<hr>");
            double doZaplaty1 = report.getRozliczenieMiesieczne().getDoZaplatyRodzic1(report.getZbiorZdarzen().getDziecko().getKey().getId());
            out.println("Kwota do zapłaty (na początek okresu): " + StringToolbox.d2c(doZaplaty1));

            out.println(HTMLRaport.getEndDocument());
        } catch (Exception ex) {
            ex.printStackTrace(out);
        } finally {
            out.close();
        }
    }

    private void exportLOG(HttpServletResponse response, KidsReport report) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(HTMLRaport.getStartDocument("Lista zdarzeń", true));
            out.println("<center><div class=\"text\">");
            out.println("<h2>Lista zdarzeń</h2>");
            out.println("<table border=1 class=\"tablesorter\">");
            out.println("<thead><tr>");
            out.println("<td>Id</td>");
            out.println("<td>Typ zdarzenia</td>");
            out.println("<td>Czas zdarzenia</td>");
            out.println("<td>Numer karty</td>");
            out.println("<td>Posiadacz karty</td>");
            out.println("</tr></thead>");
            for (Zdarzenie z : report.getZbiorZdarzen().getZdarzenia()) {
                out.println("<tr>");
                out.println("<td>" + z.getKey().getId() + "</td>");
                out.println("<td>" + z.getTypZdarzenia() + "</td>");
                out.println("<td>" + DateToolbox.getFormatedDate("yyyy-MM-dd HH:mm:ss", z.getCzasZdarzenia()) + "</td>");
                Karta karta = report.getZbiorZdarzen().getPrzedszkole().getKarta(z.getKartaKey());
                out.println("<td>" + karta.getNumerKarty() + "</td>");
                out.println("<td>" + (karta.getPosiadacz() != null ? karta.getPosiadacz() : "niezdefiniowany") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</div></center>");
            out.println("<div>Uwaga: Powyższa tabela uwzględnia wyłączni dane z rjstartorów jakie zostały uprzednie wysłane do GAE.</div>");
        } catch (Exception ex) {
            ex.printStackTrace(out);
        } finally {
            out.println(HTMLRaport.getEndDocument());
            out.close();
        }
    }
}
