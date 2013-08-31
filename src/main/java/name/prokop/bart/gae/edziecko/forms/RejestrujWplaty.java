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
package name.prokop.bart.gae.edziecko.forms;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.Wplata;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.d2s;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.pd;

public class RejestrujWplaty extends EDzieckoServlet {

    private static final long serialVersionUID = -5990937073304025867L;

    @Override
    public void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        StringBuilder out = response.getOut();

        out.append("<h1>Księgowanie wpłat (automatyczny rozdział na opieke i żywienie)</h1>");

        out.append("<form action=\"RejestrujWplaty\" method=\"post\">");

        out.append("Data księgowania:<input type=\"text\" name=\"data\" value=\"").append(DateToolbox.getFormatedDate("yyyy-MM-dd", new Date())).append("\"></td>");
        out.append("<table border=1 cellspacing=0 cellpadding=1>");
        out.append("<thead><tr>");

        out.append("<td>Dziecko</td>");
        out.append("<td>Pesel</td>");
        out.append("<td>Grupa</td>");

        out.append("<td>Wplata Całkowita</td>");

        out.append("<td>BO Opieka</td>");
        out.append("<td>Wpłaty Opieka</td>");
        out.append("<td>BZ Opieka</td>");

        out.append("<td>BO Żywienie</td>");
        out.append("<td>Wpłaty Żywienie</td>");
        out.append("<td>BZ Żywienie</td>");

        out.append("</tr></thead>\n");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            out.append("<tr>");
            Dziecko dziecko = przedszkole.getDziecko(dzieckoKey);
            Wplata wplaty = rozliczenieMiesieczne.getWplaty().liczWplateSumaryczna(dziecko.getKey().getId());
            long dzieckoId = dziecko.getKey().getId();

            out.append("<td>").append(dziecko.getImieNazwisko()).append("</td>");
            out.append("<td>").append(dziecko.getPesel() != null ? dziecko.getPesel() : "").append("</td>");
            out.append("<td>").append(dziecko.getGrupa() != null ? dziecko.getGrupa().getCategory() : "").append("</td>");

            out.append("<td><input type=\"text\" name=\"W_").append(dzieckoId).append("\" value=\"0.00\"></td>");

            out.append("<td>").append(d2s(rozliczenieMiesieczne.getBilansOtwarcia().getOpieka(dzieckoId))).append("</td>");
            out.append("<td>").append(d2s(wplaty.getOpieka())).append("</td>");
            out.append("<td>").append(d2s(rozliczenieMiesieczne.getSaldoOpieka(dzieckoId))).append("</td>");

            out.append("<td>").append(d2s(rozliczenieMiesieczne.getBilansOtwarcia().getZywienie(dzieckoId))).append("</td>");
            out.append("<td>").append(d2s(wplaty.getZywienie())).append("</td>");
            out.append("<td>").append(d2s(rozliczenieMiesieczne.getSaldoZywienie(dzieckoId))).append("</td>");

            out.append("</tr>\n");
        }

        out.append("</table><input type=\"submit\" value=\"Księguj\">");
        out.append("</form>");
    }

    @Override
    public void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        StringBuilder out = response.getOut();

        out.append("<a href=\"Rozliczenie?rokMiesiac=").append(rozliczenieMiesieczne.getRokMiesiac()).append("\">Powrót do rozliczeń</a>");
        out.append("<h2>Wprowadzone wpłaty w okresie od ").append(DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacFrom())).append(" do ").append(DateToolbox.getFormatedDate("yyyy-MM-dd", request.decodeRokMiesiacTo())).append("</h2>");

        Date dataKsiegowania = DateToolbox.parseDateChecked("yyyy-MM-dd", request.getParameter("data"));
        if (dataKsiegowania.before(request.decodeRokMiesiacFrom())) {
            out.append("Data księgowania przed okresem");
            return;
        }
        if (dataKsiegowania.after(request.decodeRokMiesiacTo())) {
            out.append("Data księgowania po okresie");
            return;
        }

        ArrayList<Wplata> wplaty = new ArrayList<Wplata>();
        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        for (Key dzieckoKey : listaDzieci) {
            double w = 0.0, o, z;
            try {
                w = pd(request.getParameter("W_" + dzieckoKey.getId()));
            } catch (Exception e) {
            }

            if (w > 0.0) {
                Wplata wplata = new Wplata();
                wplata.setDzieckoKey(dzieckoKey.getId());
                wplata.setDzienZaplaty(dataKsiegowania);

                double s = -rozliczenieMiesieczne.getSaldoOpieka(dzieckoKey.getId());
                if (w >= s) {
                    o = s;
                } else {
                    o = w;
                }

                z = w - o;
                wplata.setOpieka(o);
                wplata.setZywienie(z);

                rozliczenieMiesieczne.getWplaty().getPlatnosci().add(wplata);
                wplaty.add(wplata);
            }
        }
        rozliczenieMiesieczne.getWplaty().sortuj();
        IC.INSTANCE.replace(rozliczenieMiesieczne);

        out.append("<table border=1 cellspacing=0 cellpadding=1>");
        out.append("<thead><tr>");
        out.append("<td>Dziecko</td>");
        out.append("<td>Pesel</td>");
        out.append("<td>Grupa</td>");
        out.append("<td>Opieka</td>");
        out.append("<td>Żywienie</td>");
        out.append("<td>Razem</td>");
        out.append("</tr></thead>\n");

        double oS = 0.0, zS = 0.0;
        for (Wplata w : wplaty) {
            out.append("<tr>");
            Dziecko d = przedszkole.getDziecko(KeyFactory.createKey(rozliczenieMiesieczne.getPrzedszkoleKey(), "Dziecko", w.getDzieckoKey()));
            out.append("<td>").append(d.getImieNazwisko()).append("</td>");
            out.append("<td>").append(d.getPesel() != null ? d.getPesel() : "").append("</td>");
            out.append("<td>").append(d.getGrupa() != null ? d.getGrupa().getCategory() : "").append("</td>");
            out.append("<td>").append(StringToolbox.d2c(w.getOpieka())).append("</td>");
            out.append("<td>").append(StringToolbox.d2c(w.getZywienie())).append("</td>");
            out.append("<td>").append(StringToolbox.d2c(w.getOpieka() + w.getZywienie())).append("</td>");
            out.append("</tr>\n");
            oS += w.getOpieka();
            zS += w.getZywienie();
        }

        out.append("<tr>");
        out.append("<td>RAZEM:</td>");
        out.append("<td></td>");
        out.append("<td></td>");
        out.append("<td>").append(StringToolbox.d2c(oS)).append("</td>");
        out.append("<td>").append(StringToolbox.d2c(zS)).append("</td>");
        out.append("<td>").append(StringToolbox.d2c(oS + zS)).append("</td>");
        out.append("</tr>");

        out.append("</table>");
        out.append("<a href=\"Rozliczenie?rokMiesiac=").append(rozliczenieMiesieczne.getRokMiesiac()).append("\">Powrót do rozliczeń</a>");
    }

    @Override
    protected String getTitle() {
        return "Wprowadzanie wpłat";
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
