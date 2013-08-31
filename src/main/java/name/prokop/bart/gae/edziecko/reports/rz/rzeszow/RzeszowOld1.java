package name.prokop.bart.gae.edziecko.reports.rz.rzeszow;

import java.util.*;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

public class RzeszowOld1 extends DzienPobytuDziecka {

    private final static List<String> COLUMNS = new ArrayList<String>();
    private final static Map<String, TypKolumny> COLTYPE = new HashMap<String, TypKolumny>();
    private final static String GODZINY = "Czas dodatkowy";
    private final static String CENAGODZ = "Stawka";
    private final static String POBYT = "Opieka";
    private final static String ZYWIENIE = "Żywienie";

    static {
        COLUMNS.add(GODZINY);
        COLUMNS.add(CENAGODZ);
        COLUMNS.add(POBYT);
        COLUMNS.add(ZYWIENIE);
        COLTYPE.put(GODZINY, TypKolumny.OkresCzasu);
        COLTYPE.put(CENAGODZ, TypKolumny.Kwota);
        COLTYPE.put(POBYT, TypKolumny.Kwota);
        COLTYPE.put(ZYWIENIE, TypKolumny.Kwota);
    }
    private final Date TMIDD;
    private final Date TBEGD;
    private final Date TENDD;
    private final int YEAR;
    private final Map<String, Object> cols = new HashMap<String, Object>();

    public RzeszowOld1(KidsReport raport, Dziecko dziecko, Date data, NavigableSet<Zdarzenie> zdarzenia, Map<String, Object> shared) {
        super(raport, dziecko, data, zdarzenia, shared);
        TMIDD = DateToolbox.getBeginingOfDay(data, 11 * HOUR + 15 * MIN);
        TBEGD = DateToolbox.getBeginingOfDay(data, 06 * HOUR + 30 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(data, 17 * HOUR + 00 * MIN);
        YEAR = DateToolbox.getYear(data);
        calc();
    }

    private void calc() {
        if (czasOd.equals(czasDo)) {
            if (czasOd.before(TMIDD)) {
                czasDo = TENDD;
                uwagi = "Brak wieczora (ustalono 17:00)";
            } else {
                czasOd = TBEGD;
                uwagi = "Brak poranku (ustalono 6:30)";
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

        double stawka = -1.0;
        if (YEAR == 2011) {
            if (czasPobytu <= HOUR * 6) {
                stawka = 0.0;
            } else if (czasPobytu <= HOUR * 7) {
                stawka = BPMath.roundCurrency(2.6334);
            } else if (czasPobytu <= HOUR * 8) {
                stawka = BPMath.roundCurrency(2.3562);
            } else if (czasPobytu <= HOUR * 9) {
                stawka = BPMath.roundCurrency(2.079);
            } else {
                stawka = BPMath.roundCurrency(1.8018);
            }
        }
        if (YEAR == 2012) {
            if (czasPobytu <= HOUR * 6) {
                stawka = 0.0;
            } else if (czasPobytu <= HOUR * 7) {
                stawka = 2.85;
            } else if (czasPobytu <= HOUR * 8) {
                stawka = 2.55;
            } else if (czasPobytu <= HOUR * 9) {
                stawka = 2.25;
            } else {
                stawka = 1.95;
            }
        }

        stawka = BPMath.roundCurrency(stawka * dziecko.getRabat1AsFactor());
        cols.put(CENAGODZ, stawka);

        int czasPonad = czasPobytu - HOUR * 6;
        if (czasPonad < 0) {
            czasPonad = 0;
        }
        cols.put(GODZINY, czasPonad);
        double opieka = ((czasPonad - 1) / HOUR + 1) * stawka;
        if (czasPonad == 0) {
            opieka = 0.0;
        }
        opieka = BPMath.roundCurrency(opieka);
        cols.put(POBYT, opieka);

        double zywienie = raport.getRozliczenieMiesieczne().getParametry().getStawkaZywieniowa();
        zywienie = BPMath.roundCurrency(zywienie);
        zywienie = BPMath.roundCurrency(zywienie * dziecko.getRabat2AsFactor());
        cols.put(ZYWIENIE, zywienie);

        cenaPobytu = BPMath.roundCurrency(opieka + zywienie);
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
        return 0;
    }

    @Override
    public int getRoundFactorZywienie() {
        return 2;
    }
}
