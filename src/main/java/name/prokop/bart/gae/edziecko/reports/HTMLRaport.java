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
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class HTMLRaport {

    public static String getStringFromFile(String name) {
        InputStream resource = HTMLRaport.class.getResourceAsStream(name);
        StringBuilder b = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(resource, "UTF-8");
            char[] c = new char[100];
            int l;
            do {
                l = reader.read(c);
                if (l != -1) {
                    b.append(c, 0, l);
                }
            } while (l != -1);
        } catch (IOException e) {
            b.append(e.getMessage());
        }
        return b.toString();
    }

    public static String getStartDocument(String title, boolean includeAddsense) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">").append('\n');
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">").append('\n');
        sb.append("<head>").append('\n');
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />").append('\n');
        sb.append("<title>ISOCOM: eDziecko - ").append(title).append("</title>").append('\n');
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/style.css\" />").append('\n');
        sb.append("<script src=\"/static/calendar/js/jscal2.js\"></script>").append('\n');
        sb.append("\t<script src=\"/static/calendar/js/lang/pl.js\"></script>").append('\n');
        sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/calendar/css/jscal2.css\" />").append('\n');
        sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/calendar/css/border-radius.css\" />").append('\n');
        sb.append("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/calendar/css/steel/steel.css\" />").append('\n');
        // sb.append("\t<link rel=\"stylesheet\" href=\"/static/jquery/themes/blue/style.css\" type=\"text/css\" id=\"\" media=\"print, projection, screen\" /> ");
        // sb.append("<script type=\"text/javascript\" src=\"/static/jquery/jquery-latest.js\"></script> ");
        // sb.append("<script type=\"text/javascript\" src=\"/static/jquery/jquery.tablesorter.js\"></script> ").append('\n');
        // sb.append("<script type=\"text/javascript\"> " + "$(function() { ");
        // sb.append(" $(\"table\").tablesorter({debug: false}) " +
        // "}); </script>");
        sb.append("</head>").append('\n');
        sb.append("<body>" + "<div id=\"bg\">" + "<div id=\"wrap\">");
        sb.append("<div id=\"header\">" + "<ul id=\"nav\">" + "<li class=\"h\"><a href=\"\">Strona główna</a></li>" + "</ul>");
        sb.append("</div><!-- /header -->\n");
        sb.append(includeAddsense ? getStringFromFile("adsense.code") : "").append("\n<div id=\"content\">").append('\n');
        sb.append("<div class=\"text\">").append('\n');

        return sb.toString();
    }

    public static String getEndDocument() {
        StringBuilder sb = new StringBuilder();
        sb.append("</div>").append('\n');
        sb.append("\n</div>" + "<!-- /content -->" + "<div class=\"clearfix\"></div>" + "<div id=\"footer\">" + "<div id=\"ftinner\">" + "<div class=\"ftlink fl\">" + "<p id=\"copyright\">© 2011 by BPP. All Rights Reserved.<br/>" + "</div>" + "</div>" + "</div>" + "<!-- /footer -->" + "</div>" + "</div>" + "</body>" + "</html>");
        return sb.toString();
    }

    public static void generateZbiorowka(EDzieckoRequest request, EDzieckoResponse out) {
        KidsReport report = request.getKidsReport();
        out.println("<h2>Zbiorówka</h2>");
        out.println("<table border=1 cellspacing=0 cellpadding=1>");
        out.println("<thead><tr>");
        out.println("<td>Id</td>");
        out.println("<td>Dziecko</td>");
        out.println("<td>Pesel</td>");
        out.println("<td>Grupa</td>");
        out.println("<td>Ilość dni</td>");
        out.println("<td>Koszt</td>");
        out.println("<td>Obecność</td>");
        out.println("<td>Usuwanie obecności</td>");
        out.println("</tr></thead>");
        for (KartaPobytuDziecka kpd : report.getHumanReportsSorted1()) {
            out.println("<tr>");
            out.println("<td>" + kpd.getHuman().getKey().getId() + "</td>");
            out.println("<td><a href=\"#KD" + kpd.getHuman().getKey().getId() + "\">");
            out.println(kpd.getHuman().getImieNazwiskoAsString() + "</a></td>");
            out.println("<td>" + kpd.getHuman().getPeselAsString() + "</td>");
            out.println("<td>" + kpd.getHuman().getGrupaAsString() + "</td>");
            out.println("<td>" + kpd.getNoOfDays() + "</td>");
            out.println("<td>" + StringToolbox.d2c(kpd.getCalkowitaCenaPobytu()) + "</td>");
            out.println("<td>" + kpd.getDniPobytu() + "</td>");
            String cn = StringToolbox.cardNumberCompress(kpd.getHuman().getKarty().iterator().next().getNumerKarty());
            out.println("<td><a href=UsunZdarzenia?cn=" + cn + "&rokMiesiac=" + request.getRokMiesiac() + ">Usuń zdarzenia</a></td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("Kwota całkowita: <b>" + StringToolbox.d2c(report.getNaleznosc()) + "</b>, ");
        out.println("ilość dniówek: <b>" + report.getIloscDniowek() + "</b>.<br>");
    }

    public static void generateListyObecnosci(EDzieckoRequest request, EDzieckoResponse out) {
        KidsReport report = request.getKidsReport();
        out.println("<h2>Listy obecności</h2>");

        Date from = report.getZbiorZdarzen().getFrom();
        Date to = report.getZbiorZdarzen().getTo();
        Calendar calendar = DateToolbox.getCalendarInstance();

        out.println("<ol>");
        for (calendar.setTime(from); calendar.getTimeInMillis() <= to.getTime(); calendar.add(Calendar.DAY_OF_MONTH, 1)) {
            Date date = calendar.getTime();
            out.println("<li>Lista obecności za: " + DateToolbox.getFormatedDate("dd-MM-yyyy", date) + ". ");
            out.println("<a href=\"ListaObecnosci?dzien=" + DateToolbox.getFormatedDate("yyyyMMdd", date) + "\">Wykonaj</a>");
        }
        out.println("</ol>");

        out.println("<hr>");
    }

    public static void generateSzczegolyDziecka(EDzieckoResponse out, KartaPobytuDziecka period) {
        out.println("<a name=\"KD" + period.getHuman().getKey().getId() + "\">");
        out.println("<h2>Raport dla: " + period.getHuman().getImieNazwisko() + "</h2></a>");
        out.println("<dev>Rabat1:" + (period.getHuman().getRabat1() != null ? period.getHuman().getRabat1() : "brak") + " Rabat2:" + (period.getHuman().getRabat2() != null ? period.getHuman().getRabat2() : "brak") + "</dev>");
        out.println("<table  border=1 cellspacing=0 cellpadding=1>");
        out.println("<thead><tr>");
        out.println("<td>Data</td>");
        out.println("<td>Pobyt od</td>");
        out.println("<td>Pobyt do</td>");
        out.println("<td>Czas pobytu</td>");
        out.println("<td>Koszt</td>");
        for (String h : period.getColumnNames()) {
            out.println("<td>" + h + "</td>");
        }
        out.println("<td>Uwagi</td>");
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
            out.println("<td>" + day.getUwagi() + "</td>");
            out.println("</tr>");
        }
        out.println("<tfoot><tr>");
        out.println("<td>Razem:</td>");
        out.println("<td></td>");
        out.println("<td></td>");
        out.println("<td></td>");
        out.println("<td>" + StringToolbox.d2c(period.getCalkowitaCenaPobytu()) + "</td>");
        for (String h : period.getColumnNames()) {
            out.println("<td>" + period.sumColumn(h) + "</td>");
        }
        out.println("<td></td>");
        out.println("</tr></tfoot>");

        out.println("</table>");
        out.println("Razem: " + StringToolbox.d2c(period.getCalkowitaCenaPobytu()) + "<br>");
        out.println("Ilość dni: " + period.getNoOfDays() + "<hr>");
    }
}
