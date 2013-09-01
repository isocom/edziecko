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

import java.util.*;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.util.BPMath;

/**
 *
 * @author bart
 */
public class KidsReport {

    private final ZbiorZdarzen zbiorZdarzen;
    private final RozliczenieMiesieczne rozliczenieMiesieczne;
    private final Map<Dziecko, KartaPobytuDziecka> humanReports = new TreeMap<Dziecko, KartaPobytuDziecka>();

    public KidsReport(ZbiorZdarzen zbiorZdarzen, RozliczenieMiesieczne rozliczenieMiesieczne) {
        this.zbiorZdarzen = zbiorZdarzen;
        this.rozliczenieMiesieczne = rozliczenieMiesieczne;
        calc();
    }

    private void calc() {
        // podziel zdarzenia pomiędzy dzieci
        Map<Dziecko, List<Zdarzenie>> grouppedByPeople = new HashMap<Dziecko, List<Zdarzenie>>();
        for (Zdarzenie e : zbiorZdarzen.getZdarzenia()) {
            Dziecko dziecko = zbiorZdarzen.getPrzedszkole().getDziecko(e.getDzieckoKey());
            if (!dziecko.isAktywne()) {
                continue;
            }
            List<Zdarzenie> humanLog = grouppedByPeople.get(dziecko);
            if (humanLog == null) {
                humanLog = new ArrayList<Zdarzenie>();
                grouppedByPeople.put(zbiorZdarzen.getPrzedszkole().getDziecko(e.getDzieckoKey()), humanLog);
            }
            humanLog.add(e);
        }

        // zainicjuj indywidualne rozliczenia dzieciaków
        for (Dziecko h : grouppedByPeople.keySet()) {
            humanReports.put(h, new KartaPobytuDziecka(this, h, grouppedByPeople.get(h)));
        }
    }

    public ZbiorZdarzen getZbiorZdarzen() {
        return zbiorZdarzen;
    }

    public RozliczenieMiesieczne getRozliczenieMiesieczne() {
        return rozliczenieMiesieczne;
    }

    public int getIloscDniowek() {
        int retVal = 0;
        for (KartaPobytuDziecka d : humanReports.values()) {
            retVal += d.getDays().size();
        }
        return retVal;
    }

    public double getNaleznosc() {
        double retVal = 0.0;
        for (KartaPobytuDziecka d : humanReports.values()) {
            retVal += d.getCalkowitaCenaPobytu();
        }
        return BPMath.roundCurrency(retVal);
    }

    public Map<Dziecko, KartaPobytuDziecka> getHumanReports() {
        return humanReports;
    }

    public List<KartaPobytuDziecka> getHumanReportsSorted1() {
        ArrayList<KartaPobytuDziecka> retVal = new ArrayList<KartaPobytuDziecka>();
        retVal.addAll(humanReports.values());

        Collections.sort(retVal, new Comparator<KartaPobytuDziecka>() {
            @Override
            public int compare(KartaPobytuDziecka o1, KartaPobytuDziecka o2) {
                return Dziecko.compare(o1.getHuman(), o2.getHuman());
            }
        });
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Str. 1. Miesięczny raport z pobytu w przedszkolu.\n");
        sb.append("okres od: ").append(zbiorZdarzen.getFrom()).append("\n");
        sb.append("okres do: ").append(zbiorZdarzen.getTo()).append("\n");

        sb.append("suma należności: ").append(getNaleznosc()).append("\n");

        sb.append("Sporzadzil, Sprawdzil, Zatwierdzil, Dyrektor\n----------------\n");

        for (KartaPobytuDziecka d : humanReports.values()) {
            sb.append(d).append("\n-------------------\n");
        }
        return sb.toString();
    }
}
