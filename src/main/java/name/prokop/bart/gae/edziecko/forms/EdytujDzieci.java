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

import com.google.appengine.api.datastore.Category;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;
import static name.prokop.bart.gae.edziecko.util.StringToolbox.pd;

public class EdytujDzieci extends EDzieckoServlet {

    private static final long serialVersionUID = -3974132549782820032L;

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        Przedszkole przedszkole = request.getPrzedszkole();
        response.println("<h2>Edycja danych o dzieciach</h2>");
        response.println("<form action=\"EdytujDzieci\" method=\"post\">");
        response.println("<input type=\"submit\" value=\"Zapisz zmiany\">");
        if (request.getParameter("przedszkoleId") != null) {
            response.println("<input type=\"hidden\" name=\"przedszkoleId\" value=\"" + request.getParameter("przedszkoleId") + "\">");
        }
        response.println("<table border=1 cellspacing=0 cellpadding=1>");
        response.println("<thead><tr>");
        response.println("<td>ID</td>");
        response.println("<td>Aktywne</td>");
        response.println("<td>Dziecko</td>");
        response.println("<td>Pesel</td>");
        response.println("<td>Grupa</td>");
        response.println("<td>PIN</td>");
        response.println("<td>Rabat 1</td>");
        response.println("<td>Rabat 2</td>");
        response.println("</tr></thead>");

        for (Dziecko d : przedszkole.getDzieci()) {
            response.println("<tr>");
            response.println("<td>" + d.getKey().getId() + "</td>");
            response.println("<td><input type=\"text\" name=\"A_" + d.getKey().getId() + "\" value=\"TAK\" " + (d.isAktywne() ? "checked" : "") + "></td>");
            response.println("<td><input type=\"text\" name=\"N_" + d.getKey().getId() + "\" value=\"" + d.getImieNazwisko() + "\"></td>");
            response.println("<td>" + (d.getPesel() != null ? d.getPesel() : "") + "</td>");

            response.println("<td><select name=\"G_" + d.getKey().getId() + "\" size=\"1\">");
            x(response, d);
            response.println("</select></td>");

            response.println("<td><input type=\"text\" name=\"X_" + d.getKey().getId() + "\" value=\"" + ((d.getPin() == null) ? "" : d.getPin()) + "\"></td>");
            response.println("<td><input type=\"text\" name=\"R1_" + d.getKey().getId() + "\" value=\"" + ((d.getRabat1() != null) ? d.getRabat1() * 100.0 : "0.0") + "\">%</td>");
            response.println("<td><input type=\"text\" name=\"R2_" + d.getKey().getId() + "\" value=\"" + ((d.getRabat2() != null) ? d.getRabat2() * 100.0 : "0.0") + "\">%</td>");
            response.println("</tr>");
        }

        response.println("</table><input type=\"submit\" value=\"Zapisz zmiany\">");
        response.println("</form>");
    }

    private void x(EDzieckoResponse r, Dziecko d) {
        String[] groups = {"N/D", "I", "II", "III", "IV", "V", "VI", "VII", "VIII"};
        for (String g : groups) {
            r.println("<option value=\"" + g + "\"" + ((g.equals(d.getGrupaAsString())) ? (" selected=\"selected\"") : ("")) + ">" + g + "</option>");
        }
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        PersistenceManager pm = PMF.getPM();
        try {
            response.println("<h2>Dokonane zmiany</h2>");

            pm.currentTransaction().begin();

            Przedszkole przedszkole = request.getPrzedszkole();
            przedszkole = pm.getObjectById(Przedszkole.class, przedszkole.getKey());

            for (Dziecko d : przedszkole.getDzieci()) {
                String par, val;
                boolean bol;
                par = "A_" + d.getKey().getId();
                val = request.getParameter(par);
                if (val == null) {
                    bol = false;
                } else {
                    bol = "TAK".equals(val);
                }
                if (bol != d.isAktywne()) {
                    response.println("Zmieniono: " + d.getImieNazwisko() + " status: " + bol + "<br>");
                    d.setAktywne(bol);
                }

                par = "N_" + d.getKey().getId();
                if (request.getParameter(par) != null && !request.getParameter(par).equals(d.getImieNazwisko())) {
                    response.println("Zmieniono: " + d.getImieNazwisko() + " na: " + request.getParameter(par) + "<br>");
                    d.setImieNazwisko(request.getParameter(par));
                }

                par = "G_" + d.getKey().getId();
                if (request.getParameter(par) != null && !request.getParameter(par).equals(d.getGrupaAsString())) {
                    response.println("Zmieniono grupę: " + d.getImieNazwisko() + " na: " + request.getParameter(par) + "<br>");
                    d.setGrupa(new Category(request.getParameter(par)));
                }

                par = "X_" + d.getKey().getId();
                val = request.getParameter(par);
                if (val == null) {
                    val = "";
                }
                val = val.trim();
                if (val.length() > 0 && !val.equals(d.getPin())) {
                    response.println("Zmieniono PIN dla " + d.getImieNazwisko() + " nowy PIN: " + val + "<br>");
                    d.setPin(val);
                }

                par = "R1_" + d.getKey().getId();
                if (request.getParameter(par) != null) {
                    Double v = pd(request.getParameter("R1_" + d.getKey().getId()));
                    v /= 100.0;
                    v = BPMath.round(v, 4);
                    if (d.getRabat1() == null) {
                        d.setRabat1(0.0);
                    }
                    if (v != BPMath.round(d.getRabat1(), 4)) {
                        d.setRabat1(v);
                        response.println("Ustawiono rabat 1 dla " + d.getImieNazwisko() + " na: " + v * 100.0 + "%<br>");
                    }
                }

                par = "R2_" + d.getKey().getId();
                if (request.getParameter(par) != null) {
                    Double v = pd(request.getParameter("R2_" + d.getKey().getId()));
                    v /= 100.0;
                    v = BPMath.round(v, 4);
                    if (d.getRabat2() == null) {
                        d.setRabat2(0.0);
                    }
                    if (v != BPMath.round(d.getRabat2(), 4)) {
                        d.setRabat2(v);
                        response.println("Ustawiono rabat 2 dla " + d.getImieNazwisko() + " na: " + v * 100.0 + "%<br>");
                    }
                }
            }

            response.println("<a href=\"Menu\">Powrót do menu</a>");
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
        } catch (Exception e) {
            pm.currentTransaction().rollback();
            throw e;
        } finally {
            pm.close();
        }
    }

    @Override
    protected String getTitle() {
        return "Edycja danych dzieci";
    }

    @Override
    protected PDFType getPDFType() {
        return PDFType.Dzieci;
    }

    @Override
    protected XLSType getXLSType() {
        return XLSType.Dzieci;
    }
}
