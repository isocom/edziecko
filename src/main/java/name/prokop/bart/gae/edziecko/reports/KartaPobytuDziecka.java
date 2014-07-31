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

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class KartaPobytuDziecka {

    transient private Constructor<? extends DzienPobytuDziecka> constructor = null;
    private final KidsReport raport;
    private final Dziecko human;
    private final Set<DzienPobytuDziecka> days = new TreeSet<DzienPobytuDziecka>();
    private int roundFactorOpieka = 0;
    private int roundFactorZywienie = 2;

    KartaPobytuDziecka(KidsReport raport, Dziecko human, List<Zdarzenie> entries) {
        this.raport = raport;
        this.human = human;
        try {
            String className = human.getPrzedszkole().getKalkulatorDnia();
            @SuppressWarnings("unchecked")
            Class<? extends DzienPobytuDziecka> forName = (Class<? extends DzienPobytuDziecka>) Class.forName(className);
            constructor = forName.getConstructor(KidsReport.class, Dziecko.class, Date.class, NavigableSet.class, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        calc(entries);
    }

    private void calc(List<Zdarzenie> entries) {
        // System.out.println("entries.size(): " + entries.size());

        // 1. porozdzielaj wg. dni
        Map<Date, NavigableSet<Zdarzenie>> grouppedByDates = new TreeMap<Date, NavigableSet<Zdarzenie>>();
        for (Zdarzenie e : entries) {
            NavigableSet<Zdarzenie> humanLog = grouppedByDates.get(DateToolbox.getBeginingOfDay(e.getCzasZdarzenia()));
            if (humanLog == null) {
                humanLog = new TreeSet<Zdarzenie>();
                grouppedByDates.put(DateToolbox.getBeginingOfDay(e.getCzasZdarzenia()), humanLog);
            }
            humanLog.add(e);
        }

        // 2. Utwórz raporty dzienne
        Map<String, Object> shared = new HashMap<String, Object>();
        for (Date d : grouppedByDates.keySet()) {
            try {
                DzienPobytuDziecka dzienPobytuDziecka = constructor.newInstance(raport, human, d, grouppedByDates.get(d), shared);
                days.add(dzienPobytuDziecka);
                roundFactorOpieka = dzienPobytuDziecka.getRoundFactorOpieka();
                roundFactorZywienie = dzienPobytuDziecka.getRoundFactorZywienie();
            } catch (Exception e) {
            }
        }
    }

    public int getNoOfDays() {
        return days.size();
    }

    public double getCalkowitaCenaPobytu() {
        double retVal = 0.0;
        for (DzienPobytuDziecka d : days) {
            retVal += d.getCenaPobytu();
        }
        return BPMath.roundCurrency(retVal);
    }

    public Dziecko getHuman() {
        return human;
    }

    public Set<DzienPobytuDziecka> getDays() {
        return days;
    }

    public String getDniPobytu() {
        StringBuilder sb = new StringBuilder();
        for (DzienPobytuDziecka d : days) {
            sb.append(DateToolbox.getDayNumber(d.getData())).append(", ");
        }
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    public String sumColumn(String columnName) {
        TypKolumny typKolumny = getColumnTypes().get(columnName);
        if (typKolumny == null) {
            return "";
        }
        switch (typKolumny) {
            case Kwota:
                return StringToolbox.d2c(sumDoubles(columnName));
            case OkresCzasu:
                return DateToolbox.seconds2String(sumInts(columnName));
            default:
                return "";
        }
    }

    public int sumInts(String columnName) {
        int retval = 0;
        for (DzienPobytuDziecka d : days) {
            retval += d.columnAsInt(columnName);
        }
        return retval;
    }

    public double sumDoubles(String columnName) {
        double retval = 0.0;
        for (DzienPobytuDziecka d : days) {
            retval += d.columnAsDouble(columnName);
        }
        return retval;
    }

    public List<String> getColumnNames() {
        return days.iterator().next().columnNames();
    }

    public Map<String, TypKolumny> getColumnTypes() {
        return days.iterator().next().columnTypes();
    }

    public int getRoundFactorOpieka() {
        return roundFactorOpieka;
    }

    public int getRoundFactorZywienie() {
        return roundFactorZywienie;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Raport miesięczny dla: ").append(human).append("\n");
        for (DzienPobytuDziecka d : days) {
            sb.append(d).append("\n");
        }
        sb.append("Opłata za okres: ").append(getCalkowitaCenaPobytu()).append("\n");
        return sb.toString();
    }
}
