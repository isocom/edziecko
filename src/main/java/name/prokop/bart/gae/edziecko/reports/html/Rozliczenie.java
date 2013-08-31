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
package name.prokop.bart.gae.edziecko.reports.html;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.Date;
import java.util.Set;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.blobs.*;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.d2h;

public class Rozliczenie extends EDzieckoServlet {

    private static final long serialVersionUID = -6829752719326708278L;

    public static void generateRozliczenie(EDzieckoResponse out, Przedszkole przedszkole, RozliczenieMiesieczne rozliczenieMiesieczne) {
        out.println("<h1>Rozliczenie</h1><ul>");
        out.println("<li><b>BO (saldo początkowe):</b> znak (+ na czarno) nadpłata rodzica, znak (- na czerwono) zaległość, zobowiązanie rodzica.");
        out.println("<li><b>Opieka, Żywienie:</b> to odpowiednio rzeczywisty koszt sprawowania opieki lub rozliczany koszt posiłku. Odejmowany (-) jest od BO (zmniejsza nadpłatę, powiększa zaległość). Wynika wprost z przeniesienia raportu za pobyt dzieci do rozrachunków. Nie jest edytowalny w sekcji Rozliczenie.");
        out.println("<li><b>Wplaty:</b> suma wszyskich wpłat w danym okresie od rodzica. Dodawany (+) do BO (zwiększa nadpłatę czyli zmniejsza zaległość).");
        out.println("<li><b>BZ (saldo końcowe):</b> pokazuje stan rzeczywistego rozrachunku z rodzicem bez uwzględnienia zaliczki (BO - koszt + wpłata). Wynik dodatni to nadpłata rodzica. Wynik ujemny (na czerwono) oznacza zaległość rodzica. Powinien być zgodny z saldem konta księgowego");
        out.println("<li><b>Zaliczka:</b> Umożliwia wylicznie kwoty do zapłaty dla rodzica - w celu \"zebrania z góry\" odpłatności np. za żywienie. Efekt: zmniejsza nadpłatę, powiększa zaległość");
        out.println("<li>Do zapłaty: znak + nadpłata, znak - zaległość (z punktu widzenia rodzica). Sposób liczenia: BO minus \"zaliczka\". Informacyjnie dla rodzica. Od salda końcowego różni się tym, że nie podlega uzgodnieniu z księgami na koniec miesiąca. Służy wyłącznie sporzadzeniu listy do pobrania wpłat od rodziców.");
        out.println("</ul><table border=1 cellspacing=0 cellpadding=1>");
        out.println("<thead><tr><td colspan=3>Dziecko</td>");
        out.println("<td colspan=9>Opieka</td>");
        out.println("<td colspan=9>Żywienie</td>");
        out.println("<td colspan=1>Razem</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td>Imię i Nazwisko</td>");
        out.println("<td>Pesel</td>");
        out.println("<td>Grupa</td>");

        out.println("<td bgcolor=\"#22FFFF\">BO</td>");
        out.println("<td bgcolor=\"#22FFFF\">Nadpł.</td>");
        out.println("<td bgcolor=\"#22FFFF\">Zaleg.</td>");
        out.println("<td bgcolor=\"#22FFFF\">dni</td>");
        out.println("<td bgcolor=\"#22FFFF\">Opieka</td>");
        out.println("<td bgcolor=\"#22FFFF\">Wpłaty</td>");
        double s_o_bz = 0.0;
        out.println("<td bgcolor=\"#22FFFF\">BZ</td>");
        double s_o_zal = 0.0;
        out.println("<td bgcolor=\"#22FFFF\">Zalic.</td>");
        double s_o_pay = 0.0;
        out.println("<td bgcolor=\"#22FFFF\">Do zapł</td>");

        out.println("<td>BO</td>");
        out.println("<td>Nadpłata</td>");
        out.println("<td>Zaległość</td>");
        out.println("<td>dni</td>");
        out.println("<td>Żywienie</td>");
        out.println("<td>Wpłaty</td>");
        double s_z_bz = 0.0;
        out.println("<td>BZ</td>");
        double s_z_zal = 0.0;
        out.println("<td>Zalic.</td>");
        double s_z_pay = 0.0;
        out.println("<td>Do zapł</td>");
        out.println("<td>RAZEM:</td>");

        out.println("</tr></thead>");

        Set<Key> listaDzieci = rozliczenieMiesieczne.getListaDzieci();
        BilansOtwarcia bilansOtwarcia = rozliczenieMiesieczne.getBilansOtwarcia();
        Wplaty wplaty = rozliczenieMiesieczne.getWplaty();
        Zuzycie zuzycie = rozliczenieMiesieczne.getZuzycie();
        Zaliczki zaliczki = rozliczenieMiesieczne.getZaliczki();

        for (Key dzieckoKey : listaDzieci) {
            out.println("<tr>");
            Dziecko dziecko = przedszkole.getDziecko(dzieckoKey);
            Wplata wplata = wplaty.liczWplateSumaryczna(dzieckoKey.getId());

            out.println("<td>" + dziecko.getImieNazwiskoAsString() + "</td>");
            out.println("<td>" + dziecko.getPeselAsString() + "</td>");
            out.println("<td>" + dziecko.getGrupaAsString() + "</td>");

            // *********** OPIEKA
            final double o_bo = bilansOtwarcia.getOpieka(dzieckoKey.getId());
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(o_bo) + "</td>");
            out.println("<td bgcolor=\"#22FFFF\">" + (o_bo > 0 ? d2h(o_bo) : "0,00") + "</td>");
            out.println("<td bgcolor=\"#22FFFF\">" + (o_bo < 0 ? d2h(-o_bo) : "0,00") + "</td>");
            out.println("<td bgcolor=\"#22FFFF\">" + zuzycie.getDniOpieka(dzieckoKey.getId()) + "</td>");
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(zuzycie.getOpieka(dzieckoKey.getId())) + "</td>");
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(wplata.getOpieka()) + "</td>");
            final double o_bz = rozliczenieMiesieczne.getSaldoOpieka(dzieckoKey.getId());
            s_o_bz += o_bz;
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(o_bz) + "</td>");
            s_o_zal += zaliczki.getOpieka(dzieckoKey.getId());
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(zaliczki.getOpieka(dzieckoKey.getId())) + "</td>");
            final double o_pay = o_bo - zaliczki.getOpieka(dzieckoKey.getId());
            s_o_pay += o_pay;
            out.println("<td bgcolor=\"#22FFFF\">" + d2h(o_pay) + "</td>");

            // *********** ZYWIENIE
            final double z_bo = bilansOtwarcia.getZywienie(dzieckoKey.getId());
            out.println("<td>" + d2h(z_bo) + "</td>");
            out.println("<td>" + (z_bo > 0 ? d2h(z_bo) : "0,00") + "</td>");
            out.println("<td>" + (z_bo < 0 ? d2h(-z_bo) : "0,00") + "</td>");
            out.println("<td>" + zuzycie.getDniZywienie(dzieckoKey.getId()) + "</td>");
            out.println("<td>" + d2h(zuzycie.getZywienie(dzieckoKey.getId())) + "</td>");
            out.println("<td>" + d2h(wplata.getZywienie()) + "</td>");
            final double z_bz = rozliczenieMiesieczne.getSaldoZywienie(dzieckoKey.getId());
            s_z_bz += z_bz;
            out.println("<td>" + d2h(z_bz) + "</td>");
            s_z_zal += zaliczki.getZywienie(dzieckoKey.getId());
            out.println("<td>" + d2h(zaliczki.getZywienie(dzieckoKey.getId())) + "</td>");
            final double z_pay = z_bo - zaliczki.getZywienie(dzieckoKey.getId());
            s_z_pay += z_pay;
            out.println("<td>" + d2h(z_pay) + "</td>");

            out.println("<td>" + d2h(o_pay + z_pay) + "</td>");
            out.println("</tr>");
        }
        out.println("<tr>");
        out.println("<td>RAZEM</td>");
        out.println("<td>" + "</td>");
        out.println("<td>" + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(bilansOtwarcia.sumOpieka()) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(bilansOtwarcia.sumNadplataOpieka()) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(bilansOtwarcia.sumZalegloscOpieka()) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + zuzycie.sumDniOpieka() + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(zuzycie.sumOpieka()) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(wplaty.sumOpieka()) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(s_o_bz) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(s_o_zal) + "</td>");
        out.println("<td bgcolor=\"#22FFFF\">" + d2h(s_o_pay) + "</td>");

        out.println("<td>" + d2h(bilansOtwarcia.sumZywienie()) + "</td>");
        out.println("<td>" + d2h(bilansOtwarcia.sumNadplataZywienie()) + "</td>");
        out.println("<td>" + d2h(bilansOtwarcia.sumZalegloscZywienie()) + "</td>");
        out.println("<td>" + zuzycie.sumDniZywienie() + "</td>");
        out.println("<td>" + d2h(zuzycie.sumZywienie()) + "</td>");
        out.println("<td>" + d2h(wplaty.sumZywienie()) + "</td>");
        out.println("<td>" + d2h(s_z_bz) + "</td>");
        out.println("<td>" + d2h(s_z_zal) + "</td>");
        out.println("<td>" + d2h(s_z_pay) + "</td>");

        out.println("<td>" + d2h(s_o_pay + s_z_pay) + "</td>");
        out.println("</tr>");
        out.println("</table>");
    }

    public static void generateWplaty(EDzieckoResponse out, Przedszkole przedszkole, RozliczenieMiesieczne rozliczenieMiesieczne) {
        out.println("<h1>Zestawienie wpłat</h1>");
        out.println("<table border=1 cellspacing=0 cellpadding=1>");
        out.println("<thead><tr>");
        out.println("<td>Dziecko</td>");
        out.println("<td>Pesel</td>");
        out.println("<td>Grupa</td>");
        out.println("<td>Data wpłaty</td>");
        double sO = 0.0;
        out.println("<td>Opieka - zmiana</td>");
        double sOwp = 0.0;
        out.println("<td>Opieka - wpłaty</td>");
        double sOwy = 0.0;
        out.println("<td>Opieka - wypłaty</td>");
        double sZ = 0.0;
        out.println("<td>Żywienie - zmiana</td>");
        double sZwp = 0.0;
        out.println("<td>Żywienie - wpłata</td>");
        double sZwy = 0.0;
        out.println("<td>Żywienie - wypłata</td>");
        out.println("<td>Razem zmiana</td>");
        out.println("<td>Akcja</td>");

        Wplaty wplaty = rozliczenieMiesieczne.getWplaty();
        Date dataPoprzedniejWplaty = null;
        for (int i = 0; i < wplaty.getPlatnosci().size(); i++) {
            Wplata w = wplaty.getPlatnosci().get(i);
            if (dataPoprzedniejWplaty != null && dataPoprzedniejWplaty.before(w.getDzienZaplaty())) {
                subWplaty(out, wplaty, dataPoprzedniejWplaty);
            }
            out.println("<tr>");
            Dziecko dziecko = przedszkole.getDziecko(KeyFactory.createKey(rozliczenieMiesieczne.getPrzedszkoleKey(), "Dziecko", w.getDzieckoKey()));
            out.println("<td>" + dziecko.getImieNazwisko() + "</td>");
            out.println("<td>" + (dziecko.getPesel() != null ? dziecko.getPesel() : "") + "</td>");
            out.println("<td>" + (dziecko.getGrupa() != null ? dziecko.getGrupa().getCategory() : "") + "</td>");
            out.println("<td>" + DateToolbox.getFormatedDate("yyyy-MM-dd", w.getDzienZaplaty()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getOpieka()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getOpiekaWplata()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getOpiekaWyplata()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getZywienie()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getZywienieWplata()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getZywienieWyplata()) + "</td>");
            out.println("<td>" + StringToolbox.d2c(w.getOpieka() + w.getZywienie()) + "</td>");
            sO += w.getOpieka();
            sOwp += w.getOpiekaWplata();
            sOwy += w.getOpiekaWyplata();
            sZ += w.getZywienie();
            sZwp += w.getZywienieWplata();
            sZwy += w.getZywienieWyplata();
            out.println("<td><a href=\"UsunWplate?indexWplaty=" + wplaty.getPlatnosci().indexOf(w) + "\">" + "Usuń wpłatę</a></td>");
            out.println("</tr>");
            if (i + 1 == wplaty.getPlatnosci().size()) {
                subWplaty(out, wplaty, w.getDzienZaplaty());
            }
            dataPoprzedniejWplaty = w.getDzienZaplaty();
        }

        out.println("<tr>");
        out.println("<td colspan=4>RAZEM WPŁATY:</td>");
        out.println("<td>" + StringToolbox.d2c(sO) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sOwp) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sOwy) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sZ) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sZwp) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sZwy) + "</td>");
        out.println("<td>" + StringToolbox.d2c(sO + sZ) + "</td>");
        out.println("<td>" + "</td>");
        out.println("</tr>");
        out.println("</table>");
    }

    private static void subWplaty(EDzieckoResponse out, Wplaty wplaty, Date date) {
        out.println("<tr>");
        out.println("<td colspan=3>" + "razem wplaty z dnia:" + "</td>");
        out.println("<td>" + DateToolbox.getFormatedDate("yyyy-MM-dd", date) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaOpieka(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaOpiekaWplata(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaOpiekaWyplata(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaZywienie(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaZywienieWplata(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDziennaZywienieWyplata(date)) + "</td>");
        out.println("<td>" + StringToolbox.d2c(wplaty.liczDzienna(date)) + "</td>");
        out.println("<td>" + "--------" + "</td>");
        out.println("</tr>");
    }

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        RozliczenieMiesieczne rozliczenieMiesieczne = request.retrieveRozliczenieMiesieczne();
        Date from = request.decodeRokMiesiacFrom();
        Date to = request.decodeRokMiesiacTo();

        Przedszkole przedszkole = request.getPrzedszkole();

        response.println("<h1>Rozrachunki</h1>");
        response.println("<pre>" + przedszkole + "</pre>");
        response.println("<a href=\"" + request.getLogoutUrl() + "\">Wyloguj z bieżącego przedszkola</a><hr>");
        response.println("<h2>Rozliczanie od " + DateToolbox.getFormatedDate("yyyy-MM-dd", from) + " do " + DateToolbox.getFormatedDate("yyyy-MM-dd", to) + "</h2>");
        response.println("<a href=\"WprowadzBO\">Wprowadz/Popraw Bilans Otwarcia</a><br>");
        response.println("<a href=\"KsiegujWplaty\">Księguj wpłaty</a>, <a href=\"RejestrujWplaty\">Księguj inteligentnie wpłaty</a><br>");
        response.println("<a href=\"UstawZaliczki\">Ustaw zaliczki</a><br>");
        response.println("<a href=\"UstalParametry\">Ustaw parametry</a><br>");
        response.println("<a href=\"EdycjaZywienia\">Edytuj żywienie</a><br>");
        response.println("<a href=\"javascript:pobierzBZ()\">Pobierz BZ z poprzedniego okresu</a><br>");
        response.println("<a href=\"Raporty\">Powrót do raportów</a><br>");
        response.println("<a href=\"PDF?type=" + PDFType.PaskiDlaRodzicow.name() + "\">Paski dla rodziców</a>");
        generateRozliczenie(response, przedszkole, rozliczenieMiesieczne);
        generateWplaty(response, przedszkole, rozliczenieMiesieczne);
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
    }

    @Override
    protected String getTitle() {
        return "Rozliczenie miesięczne / rozrachunki";
    }

    @Override
    protected PDFType getPDFType() {
        return PDFType.Rozliczenie;
    }

    @Override
    protected String getScript() {
        StringBuilder sb = new StringBuilder();
        sb.append("function pobierzBZ() {");
        sb.append("  if (confirm(\"Czy potwierdzasz wczytanie BZ?\")) {");
        sb.append("    location='PobierzBZ';");
        sb.append("  }");
        sb.append("}");
        return sb.toString();
    }

    @Override
    protected XLSType getXLSType() {
        return XLSType.Rozliczenie;
    }
}
