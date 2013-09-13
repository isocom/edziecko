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
package name.prokop.bart.gae.edziecko.appletsrv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.jdo.PersistenceManager;
import name.prokop.bart.gae.edziecko.bol.*;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;
import name.prokop.bart.gae.edziecko.util.mailer.ExceptionReportMail;
import name.prokop.bart.gae.edziecko.util.mailer.Mail;
import name.prokop.bart.gae.edziecko.util.mailer.MailAddress;
import name.prokop.bart.gae.edziecko.util.mailer.Mailer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppletDAO {

    static boolean storeLog(Przedszkole przedszkole, String tibboDatabase) {
        Set<Zdarzenie> zdarzenia = new TreeSet<Zdarzenie>();
        StringBuilder ERRORS = new StringBuilder();

        try {
            JSONObject database = new JSONObject(tibboDatabase);
            JSONArray log = database.getJSONArray("log");
            for (int i = 0; i < log.length(); i++) {
                JSONObject logEntry = log.getJSONObject(i);
                Zdarzenie zdarzenie = new Zdarzenie();
                Karta karta = przedszkole.getKartaBySN(logEntry.getString("c"));
                if (karta == null) {
                    ERRORS.append(logEntry.toString()).append('\n');
                    continue;
                }
                zdarzenie.setKartaKey(karta.getKey());
                zdarzenie.setDzieckoKey(karta.getDziecko().getKey());
                zdarzenie.setPrzedszkoleKey(karta.getDziecko().getPrzedszkole().getKey());
                zdarzenie.setCzasZdarzenia(new Date(logEntry.getLong("t")));
                zdarzenie.setTypZdarzenia(TypZdarzenia.PrzylozenieKarty);
                zdarzenia.add(zdarzenie);
            }
        } catch (JSONException jex) {
            Mailer.INSTANCE.send(new ExceptionReportMail(jex));
            return false;
        }

        Set<String> okresy = new HashSet<String>();
        PersistenceManager pm = PMF.getPM();
        for (Zdarzenie z : zdarzenia) {
            z.persist(pm);
            okresy.add(DateToolbox.getFormatedDate("yyyyMM", z.getCzasZdarzenia()));
        }
        pm.close();
        for (String o : okresy) {
            IC.INSTANCE.remove(ZbiorZdarzen.buildID(przedszkole.getKey(), Integer.parseInt(o)));
        }

        if (ERRORS.length() > 0) {
            Mail mail = new Mail();
            mail.addTo(new MailAddress("EDZIECKO", "edziecko@isocom.eu"));
            mail.setSubject("Problem z odczytem zdarzen - brak karty w systemie.");
            ERRORS.insert(0, "Dotyczy: " + przedszkole + "\n\n");
            mail.setTextBody(ERRORS.toString());
            Mailer.INSTANCE.send(mail);
        }

        return true;
    }

    static JSONObject produceTibboDatabase(Przedszkole przedszkole) {
        try {
            JSONObject tibboDatabase = new JSONObject();
            JSONArray jsonDzieci = new JSONArray();
            JSONArray jsonKarty = new JSONArray();

            int dzieckoIdx = 0;
            // int kartaIdx = 0;
            for (Dziecko dziecko : przedszkole.getDzieci()) {
                if (!dziecko.isAktywne()) {
                    continue;
                }
                if (dziecko.getKarty().isEmpty()) {
                    continue;
                }
                JSONObject jsonDziecko = new JSONObject();
                jsonDziecko.put("pos", ++dzieckoIdx);
                jsonDziecko.put("name", dziecko.getImieNazwisko());
                jsonDzieci.put(jsonDziecko);
                for (Karta karta : dziecko.getKarty()) {
                    if (!karta.isAktywna()) {
                        continue;
                    }
                    JSONObject jsonKarta = new JSONObject();
                    // jsonKarta.put("pos", ++kartaIdx);
                    jsonKarta.put("s", karta.getNumerSeryjny().substring(2));
                    jsonKarta.put("t", karta.getNumerSeryjny().substring(0, 2));
                    jsonKarta.put("h", dzieckoIdx);
                    jsonKarty.put(jsonKarta);
                }
            }

            tibboDatabase.putOpt("TibboHumans", jsonDzieci);
            tibboDatabase.putOpt("TibboCards", jsonKarty);
            return tibboDatabase;
        } catch (JSONException jex) {
            return null;
        }
    }
}
