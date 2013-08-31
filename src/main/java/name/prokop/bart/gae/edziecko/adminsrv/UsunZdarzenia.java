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

import com.google.appengine.api.datastore.KeyFactory;
import java.util.List;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.bol.cacheable.CacheableZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;

public class UsunZdarzenia extends EDzieckoServlet {

    private static final long serialVersionUID = 1914119596708215352L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        if (request.getParameter("zdarzenieId") != null) {
            Long zdarzenieId = Long.parseLong(request.getParameter("zdarzenieId"));
            PersistenceManager pm = PMF.getPM();
            try {
                Zdarzenie z = pm.getObjectById(Zdarzenie.class, KeyFactory.createKey("Zdarzenie", zdarzenieId));

                String key = ZbiorZdarzen.buildID(z.getPrzedszkoleKey(), Integer.parseInt(DateToolbox.getFormatedDate("yyyyMM", z.getCzasZdarzenia())));
                CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
                if (czz != null) {
                    List<Zdarzenie> zz = czz.decode();
                    zz.remove(z);
                    czz = new CacheableZbiorZdarzen(z.getPrzedszkoleKey().getId(), zz);
                    IC.INSTANCE.store(key, czz);
                }

                pm.deletePersistent(z);
            } finally {
                pm.close();
            }
            response.sendRedirect("Raporty");
            return;
        }

        Przedszkole przedszkole = request.getPrzedszkole();
        String cn = StringToolbox.cardNumberPretty(request.getParameter("cn"));
        ZbiorZdarzen zz = new ZbiorZdarzen(cn, request.getRokMiesiac());

        response.println("<h2>Lista zdarzeń</h2>");
        response.println("<table border=1 class=\"tablesorter\">");
        response.println("<thead><tr>");
        response.println("<td>Id</td>");
        response.println("<td>Typ zdarzenia</td>");
        response.println("<td>Czas zdarzenia</td>");
        response.println("<td>Numer karty</td>");
        response.println("<td>Posiadacz karty</td>");
        response.println("<td>Usuń</td>");
        response.println("</tr></thead>");
        for (Zdarzenie z : zz.getZdarzenia()) {
            response.println("<tr>");
            response.println("<td>" + z.getKey().getId() + "</td>");
            response.println("<td>" + z.getTypZdarzenia() + "</td>");
            response.println("<td>" + DateToolbox.getFormatedDate("yyyy-MM-dd HH:mm:ss", z.getCzasZdarzenia()) + "</td>");
            Karta karta = przedszkole.getKarta(z.getKartaKey());
            if (karta != null) {
                response.println("<td>" + karta.getNumerKarty() + "</td>");
                response.println("<td>" + (karta.getPosiadacz() != null ? karta.getPosiadacz() : "niezdefiniowany") + "</td>");
            } else {
                response.println("<td>NULL karta</td>");
                response.println("<td>" + z.getKartaKey() + "</td>");
            }
            response.println("<td><a href=UsunZdarzenia?zdarzenieId=" + z.getKey().getId() + ">Usuń to zdarzenie</td>");
            response.println("</tr>");
        }
        response.println("</table>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "Usuwanie zdarzeń";
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
