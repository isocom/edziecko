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
package name.prokop.bart.gae.edziecko.reports.rz.swilcza;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TimeZone;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import static name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka.HOUR;
import static name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka.MIN;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

public class Dabrowa extends DzienPobytuDziecka {

    private final static List<String> COLUMNS = new ArrayList<>();
    private final static Map<String, TypKolumny> COLTYPE = new HashMap<>();
    private final static String CZASPRZED = "Czas przed";
    private final static String CZASPO = "Czas po";
    private final static String CZAS_NAR = "Czas nar.";
    private final static String POBYT = "Opieka";
    private final static String POBYT_NAR = "Opieka nar.";
    private final static String ZYWIENIE = "Żywienie";

    static {
        COLUMNS.add(CZASPRZED);
        COLUMNS.add(CZASPO);
        COLUMNS.add(POBYT);
        COLUMNS.add(ZYWIENIE);
        COLUMNS.add(CZAS_NAR);
        COLUMNS.add(POBYT_NAR);
        COLTYPE.put(CZASPRZED, TypKolumny.OkresCzasu);
        COLTYPE.put(CZASPO, TypKolumny.OkresCzasu);
        COLTYPE.put(ZYWIENIE, TypKolumny.Kwota);
        COLTYPE.put(POBYT, TypKolumny.Kwota);
        COLTYPE.put(CZAS_NAR, TypKolumny.OkresCzasuBezSumowania);
        COLTYPE.put(POBYT_NAR, TypKolumny.KwotaBezSumowania);
    }
    private final double STAWKA_OPIEKI;
    private final Date TBEGF;
    private final Date TENDF;
    private final Date TBEGD;
    private final Date TMIDD;
    private final Date TENDD;
    private final int YEAR;
    private final int MONTH;
    //private final int dayOfWeek;
    private final Map<String, Object> cols = new HashMap<>();

    public Dabrowa(KidsReport raport, Dziecko dziecko, Date date, NavigableSet<Zdarzenie> entries, Map<String, Object> shared) {
        super(raport, dziecko, date, entries, shared);

        if (shared.get(CZAS_NAR) == null) {
            shared.put(CZAS_NAR, new Integer(0));
        }

        TBEGD = DateToolbox.getBeginingOfDay(date, 06 * HOUR + 30 * MIN);
        TMIDD = DateToolbox.getBeginingOfDay(date, 11 * HOUR + 30 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 45 * MIN);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        calendar.setTime(date);

        STAWKA_OPIEKI = 1.0;

        TBEGF = DateToolbox.getBeginingOfDay(date, 8 * HOUR + 0 * MIN);
        TENDF = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 12 * MIN);

        YEAR = DateToolbox.getYear(data);
        MONTH = DateToolbox.getMonth(data);
        calc();
    }

    private void calc() {
        if (czasOd.equals(czasDo)) {
            if (czasOd.before(TMIDD)) {
                czasDo = TENDD;
                uwagi = "Brak wieczora (ustalono 16:30)";
            } else {
                czasOd = TBEGD;
                uwagi = "Brak poranku (ustalono 6:00)";
            }
        }

        if (czasDo.before(TMIDD)) {
            czasDo = TENDD;
            uwagi = "Brak wieczora (ostatnie zdarzenie < 11:15)";
        }
        if (czasOd.after(TMIDD)) {
            czasOd = TBEGD;
            uwagi = "Brak poranku (pierwsze zdarzenie > 11:15)";
        }

        czasPobytu = (int) (czasDo.getTime() / 1000 - czasOd.getTime() / 1000);

        int czasPrzed = (int) (TBEGF.getTime() / 1000 - czasOd.getTime() / 1000);
        int czasPo = (int) (czasDo.getTime() / 1000 - TENDF.getTime() / 1000);
        if (czasPrzed < 0) {
            czasPrzed = 0;
        }
        if (czasPo < 0) {
            czasPo = 0;
        }

        // czas za ktory liczymy
        int czasNaliczony = czasPo + czasPrzed;
        shared.put(CZAS_NAR, new Integer(czasNaliczony + (Integer) shared.get(CZAS_NAR)));

        double zywienie = raport.getRozliczenieMiesieczne().getParametry().getStawkaZywieniowa();
        zywienie = BPMath.roundCurrency(zywienie * dziecko.getRabat2AsFactor());

        cols.put(CZASPRZED, czasPrzed);
        cols.put(CZASPO, czasPo);
        cols.put(POBYT, new Double(0.0));
        cols.put(ZYWIENIE, zywienie);
        cols.put(CZAS_NAR, (Integer) shared.get(CZAS_NAR));

        double opieka = (((Integer) shared.get(CZAS_NAR) - 1) / MIN + 1) * (STAWKA_OPIEKI / 60);
        if ((Integer) shared.get(CZAS_NAR) == 0) {
            opieka = 0.0;
        }
        opieka = BPMath.roundCurrency(opieka * dziecko.getRabat1AsFactor());
        cols.put(POBYT_NAR, opieka);

        if (shared.get("cols") != null) {
            Map<String, Object> prev_cols = (Map<String, Object>) shared.get("cols");
            prev_cols.put(POBYT, new Double(0.0));
            Dabrowa b = (Dabrowa) shared.get("prev");
            b.setCenaPobytu((Double) prev_cols.get(ZYWIENIE));
        }
        shared.put("cols", cols);
        shared.put("prev", this);

        cols.put(POBYT, cols.get(POBYT_NAR));
        cenaPobytu = zywienie + (Double) cols.get(POBYT_NAR);
    }

    @Override
    public List<String> columnNames() {
        return COLUMNS;
    }

    @Override
    public Map<String, Object> columnValues() {
        return cols;
    }

    @Override
    public Map<String, TypKolumny> columnTypes() {
        return COLTYPE;
    }

    @Override
    public int getRoundFactorOpieka() {
        return 2;
    }

    @Override
    public int getRoundFactorZywienie() {
        return 2;
    }
}
