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

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.*;

public class DodajDziecko extends EDzieckoServlet {

    private static final long serialVersionUID = 1914119596708215352L;

    public static String calculateNextCardNumber(Przedszkole przedszkole) {
        List<String> cardNumbers = new ArrayList<String>();
        for (Dziecko d : przedszkole.getDzieci()) {
            for (Karta k : d.getKarty()) {
                String numerek = k.getNumerKarty();
                numerek = StringToolbox.cardNumberCompress(numerek);
                if (numerek.charAt(6) != '0') {
                    continue;
                }
                cardNumbers.add(numerek);
            }
        }
        Collections.sort(cardNumbers, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        String lastCard = cardNumbers.get(cardNumbers.size() - 1);
        lastCard = lastCard.substring(7, lastCard.length() - 1);
        int prefix = Integer.parseInt(lastCard.substring(0, 4));
        int number = Integer.parseInt(lastCard.substring(4, 8));
        lastCard = CardNumberToolbox.generateType0(prefix, number + 1);
        lastCard = StringToolbox.cardNumberPretty(lastCard);
        return lastCard;
    }

    @Override
    protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        String c = calculateNextCardNumber(request.getPrzedszkole());
        response.println("Następny numer karty to: " + c);
        response.println("<form action=\"DodajDziecko\" method=\"post\"><table>");
        response.println("<tr><td>Imię i nazwisko dziecka:</td><td><input type=\"text\" name=\"d\" value=\"Jan Nowak\"></td></tr>");
        response.println("<tr><td>Numer karty:</td><td><input type=\"text\" name=\"cn\" value=\"" + c + "\"></td></tr>");
        response.println("<tr><td>Numer seryjny:</td><td><input type=\"text\" name=\"sn\" value=\"TMP0" + StringToolbox.generateRandomStringId(8) + "\"></td></tr>");
        response.println("<tr><td><td colspan=\"2\"><input type=\"submit\" value=\"Zrób\"></td></tr>");
        response.println("</table></form>");
    }

    @Override
    protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
        String d = request.getParameter("d").trim();
        String cn = request.getParameter("cn").trim();
        String sn = request.getParameter("sn").trim();
        PersistenceManager pm = PMF.getPM();
        try {
            pm.currentTransaction().begin();
            Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", request.getPrzedszkoleKey().getId()));
            response.println("<pre>" + przedszkole.toString() + "<pre>");
            response.println("Ilość dzieci z " + przedszkole.getDzieci().size());
            Dziecko dziecko = new Dziecko(d);
            dziecko.setGrupa(new Category("N/D"));
            dziecko.add(new Karta(sn, cn));
            przedszkole.add(dziecko);
            response.println("na: " + przedszkole.getDzieci().size()+".<br>");
            pm.currentTransaction().commit();
            IC.INSTANCE.replace(przedszkole);
            response.println("<a href=\"DodajDziecko\">Dodaj kolejne NOWE dziecko</a><br>Uwaga: Nie klikaj wstecz, tylko na tym linku, jeśli chcesz dodać kolejne dziecko!");
        } finally {
            if (pm.currentTransaction().isActive()) {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    @Override
    protected String getTitle() {
        return "Dodaj nowe dziecko";
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
