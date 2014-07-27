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

import java.util.Date;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.*;
import name.prokop.bart.gae.edziecko.bol.cacheable.CacheableZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;

public class DodajZdarzenie extends EDzieckoServlet {

    private static final long serialVersionUID = 1914119596708215352L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();

        response.println("<form action=\"DodajZdarzenie\" method=\"post\">");
        response.println("Dziecko: <select name=\"karta\" size=\"1\"><br>");
        for (Dziecko d : przedszkole.getDzieciPosortowane()) {
            if (d.getKarty().isEmpty()) {
                continue;
            }
            response.println("<option value=\"" + d.getKarty().iterator().next().getNumerSeryjny() + "\">");
            response.println(d.getImieNazwisko() + " " + d.getGrupaAsString());
            response.println("</option>");
        }
        response.println("</select>");
        Date date = (request.getSession().getAttribute("data") == null) ? new Date() : (Date) request.getSession().getAttribute("data");

        response.println("<input type=\"text\" name=\"data\" value=\"" + DateToolbox.getFormatedDate("yyyy-MM-dd", date) + "\">");
        response.println("<input type=\"text\" name=\"czas\" value=\"" + DateToolbox.getFormatedDate("HH:mm", date) + "\">");
        response.println("<input type=\"submit\" value=\"Dodaj zdarzenie\">");
        response.println("</form>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();

        String k = request.getParameter("karta");
        String s = request.getParameter("data") + " " + request.getParameter("czas");
        Date date = DateToolbox.parseDateChecked("yyyy-MM-dd HH:mm", s);
        request.getSession().setAttribute("data", date);

        if (date == null) {
            response.println("<b>Niestety nie podałeś daty !!!. Nie dodano zdarzenia</b><hr>");
            response.println("Co chcesz teraz zrobić: ");
            response.println("<a href=DodajZdarzenie>Dodać kolejne zdarzenie</a> czy ");
            response.println("<a href=Menu>Powrócić do do menu</a><br>");
            return;
        }

        PersistenceManager pm = PMF.getPM();
        try {
            Zdarzenie zdarzenie = new Zdarzenie();
            Karta karta = przedszkole.getKartaBySN(k);
            zdarzenie.setKartaKey(karta.getKey());
            zdarzenie.setDzieckoKey(karta.getDziecko().getKey());
            zdarzenie.setPrzedszkoleKey(karta.getDziecko().getPrzedszkole().getKey());
            zdarzenie.setCzasZdarzenia(date);
            zdarzenie.setTypZdarzenia(TypZdarzenia.WpisManualny);
            zdarzenie.persist(pm);

            String key = ZbiorZdarzen.buildID(karta.getDziecko().getPrzedszkole().getKey(), Integer.parseInt(DateToolbox.getFormatedDate("yyyyMM", date)));
            CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
            if (czz != null) {
                czz = new CacheableZbiorZdarzen(czz, zdarzenie);
                IC.INSTANCE.store(key, czz);
            }

            response.println("Dodano: " + zdarzenie + "<hr>");
            response.println("Co chcesz teraz zrobić: ");
            response.println("<a href=DodajZdarzenie>Dodać kolejne zdarzenie</a> czy ");
            response.println("<a href=Menu>Powrócić do do menu</a><br>");
        } finally {
            pm.close();
        }
    }

    @Override
    protected String getTitle() {
        return "Dodawanie zdarzenia";
    }

    @Override
    protected PDFType getPDFType() {
        return null;
    }

    @Override
    protected XLSType getXLSType() {
        return null;
    }
}
