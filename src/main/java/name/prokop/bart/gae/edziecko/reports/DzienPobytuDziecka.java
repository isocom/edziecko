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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

/**
 *
 * @author bart
 */
public abstract class DzienPobytuDziecka implements Comparable<DzienPobytuDziecka> {

    protected final Map<String, Object> shared;
    public final static int SEC = 1;
    public final static int MIN = 60 * SEC;
    public final static int HALF_HOUR = 30 * MIN;
    public final static int HOUR = 60 * MIN;
    protected final KidsReport raport;
    protected final Dziecko dziecko;
    protected final Date data;
    protected final NavigableSet<Zdarzenie> zdarzenia;
    private final Zdarzenie zdarzenieOd;
    private final Zdarzenie zdarzenieDo;
    protected Date czasOd;
    protected Date czasDo;
    private final Karta kartaOd;
    private final Karta kartaDo;
    private final String opiekunOd;
    private final String opiekunDo;
    protected int czasPobytu;
    protected double cenaPobytu = 0.0;
    protected String uwagi = "";

    public DzienPobytuDziecka(KidsReport raport, Dziecko dziecko, Date data, NavigableSet<Zdarzenie> zdarzenia, Map<String, Object> shared) {
        this.shared = shared;
        this.raport = raport;
        this.dziecko = dziecko;
        this.data = data;
        this.zdarzenia = zdarzenia;

        zdarzenieOd = zdarzenia.first();
        zdarzenieDo = zdarzenia.last();
        czasOd = zdarzenieOd.getCzasZdarzenia();
        czasDo = zdarzenieDo.getCzasZdarzenia();
        kartaOd = dziecko.getPrzedszkole().getKarta(zdarzenieOd.getKartaKey());
        kartaDo = dziecko.getPrzedszkole().getKarta(zdarzenieDo.getKartaKey());

        if (kartaOd.getPosiadacz() == null) {
            opiekunOd = kartaOd.getNumerKarty();
        } else {
            opiekunOd = kartaOd.getPosiadacz();
        }

        if (kartaDo.getPosiadacz() == null) {
            opiekunDo = kartaDo.getNumerKarty();
        } else {
            opiekunDo = kartaDo.getPosiadacz();
        }
    }

    public List<String> columnNames() {
        return Collections.emptyList();
    }

    public abstract Map<String, Object> columnValues();

    public abstract Map<String, TypKolumny> columnTypes();

    public abstract int getRoundFactorOpieka();

    public abstract int getRoundFactorZywienie();

    public String columnAsString(String columnName) {
        Object val = columnValues().get(columnName);
        switch (columnTypes().get(columnName)) {
            case Kwota:
            case KwotaBezSumowania:
                return StringToolbox.d2c((Double) val);
            case OkresCzasu:
            case OkresCzasuBezSumowania:
                return DateToolbox.seconds2String((Integer) val);
            default:
                return "";
        }
    }

    public int columnAsInt(String columnName) {
        return (Integer) columnValues().get(columnName);
    }

    public double columnAsDouble(String columnName) {
        return (Double) columnValues().get(columnName);
    }

    @Override
    public int compareTo(DzienPobytuDziecka o) {
        return data.compareTo(o.data);
    }

    /**
     * Strefa CET, HH==0, mm==0, ss=0
     *
     * @return data krótej dotyczy dany obiekt
     */
    public Date getData() {
        return data;
    }

    public NavigableSet<Zdarzenie> getZdarzenia() {
        return zdarzenia;
    }

    public Zdarzenie getZdarzenieOd() {
        return zdarzenieOd;
    }

    public Zdarzenie getZdarzenieDo() {
        return zdarzenieDo;
    }

    /**
     *
     * @return czas pierwszego odbicia karty
     */
    public Date getCzasOd() {
        return czasOd;
    }

    /**
     *
     * @return czas ostatniego odbicia karty
     */
    public Date getCzasDo() {
        return czasDo;
    }

    public Karta getKartaOd() {
        return kartaOd;
    }

    public Karta getKartaDo() {
        return kartaDo;
    }

    public String getOpiekunOd() {
        return opiekunOd;
    }

    public String getOpiekunDo() {
        return opiekunDo;
    }

    /**
     * w sekundach
     *
     * @return czas pobytu w sekundach
     */
    public int getCzasPobytu() {
        return czasPobytu;
    }

    public double getCenaPobytu() {
        return cenaPobytu;
    }

    public void setCenaPobytu(double cenaPobytu) {
        this.cenaPobytu = cenaPobytu;
    }

    public String getUwagi() {
        return uwagi;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DateToolbox.getFormatedDate("yyyy-MM-dd", data)).append(' ');
        sb.append(DateToolbox.getFormatedDate("HH:mm:ss", czasOd)).append(' ');
        sb.append(DateToolbox.getFormatedDate("HH:mm:ss", czasDo)).append(' ');
        sb.append(cenaPobytu).append(" zł ").append(uwagi);
        return sb.toString();
    }

    protected void dodajUwagi(String remark) {
        this.uwagi = this.uwagi + " " + remark;
    }
}
