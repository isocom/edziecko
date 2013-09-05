package name.prokop.bart.gae.edziecko.reports.rz.rzeszow;

import java.util.*;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

public class Rzeszow extends DzienPobytuDziecka {

    private final static List<String> COLUMNS = new ArrayList<String>();
    private final static Map<String, TypKolumny> COLTYPE = new HashMap<String, TypKolumny>();
    private final static String CZASPOBYTU = "Czas pobytu";
    private final static String CZASPRZED = "Czas przed";
    private final static String CZASPO = "Czas po";
    private final static String CZASPONAD = "Czas płatny";
    private final static String CENAGODZ = "Stawka";
    private final static String POBYT = "Opieka";
    private final static String ZYWIENIE = "Żywienie";

    static {
        COLUMNS.add(CZASPOBYTU);
        COLUMNS.add(CZASPRZED);
        COLUMNS.add(CZASPO);
        COLUMNS.add(CZASPONAD);
        COLUMNS.add(CENAGODZ);
        COLUMNS.add(POBYT);
        COLUMNS.add(ZYWIENIE);
        COLTYPE.put(CZASPOBYTU, TypKolumny.OkresCzasu);
        COLTYPE.put(CZASPRZED, TypKolumny.OkresCzasu);
        COLTYPE.put(CZASPO, TypKolumny.OkresCzasu);
        COLTYPE.put(CZASPONAD, TypKolumny.OkresCzasu);
        COLTYPE.put(CENAGODZ, TypKolumny.Kwota);
        COLTYPE.put(POBYT, TypKolumny.Kwota);
        COLTYPE.put(ZYWIENIE, TypKolumny.Kwota);
    }
    private final Date TBEGF;
    private final Date TENDF;
    private final Date TMIDD;
    private final Date TBEGD;
    private final Date TENDD;
    private final int YEAR;
    private final int MONTH;
    private final Map<String, Object> cols = new HashMap<String, Object>();

    public Rzeszow(KidsReport raport, Dziecko dziecko, Date data, NavigableSet<Zdarzenie> zdarzenia, Map<String, Object> shared) {
        super(raport, dziecko, data, zdarzenia, shared);
        TMIDD = DateToolbox.getBeginingOfDay(data, 11 * HOUR + 15 * MIN);
        TBEGD = DateToolbox.getBeginingOfDay(data, 06 * HOUR + 30 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(data, 17 * HOUR + 00 * MIN);
        YEAR = DateToolbox.getYear(data);
        MONTH = DateToolbox.getMonth(data);
        TBEGF = DateToolbox.getBeginingOfDay(data, 8 * HOUR + 00 * MIN);
        TENDF = DateToolbox.getBeginingOfDay(data, 13 * HOUR + 00 * MIN);
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
        cols.put(CZASPOBYTU, czasPobytu);
        int czasPrzed = (int) (TBEGF.getTime() / 1000 - czasOd.getTime() / 1000);
        int czasPo = (int) (czasDo.getTime() / 1000 - TENDF.getTime() / 1000);
        if (czasPrzed < 0) {
            czasPrzed = 0;
        }
        if (czasPo < 0) {
            czasPo = 0;
        }
        cols.put(CZASPRZED, czasPrzed);
        cols.put(CZASPO, czasPo);
        int czasPonad = czasPrzed + czasPo;

        double stawka = -1.0;
        if (YEAR == 2012) {
            if (czasPonad <= HOUR * 1) {
                stawka = 2.85;
            } else if (czasPonad <= HOUR * 2) {
                stawka = 2.55;
            } else if (czasPonad <= HOUR * 3) {
                stawka = 2.25;
            } else {
                stawka = 1.95;
            }
        }
        if (YEAR == 2013) {
            if (czasPonad <= HOUR * 1) {
                stawka = 3.04;
            } else if (czasPonad <= HOUR * 2) {
                stawka = 2.72;
            } else if (czasPonad <= HOUR * 3) {
                stawka = 2.40;
            } else {
                stawka = 2.08;
            }
        }
        if ((YEAR == 2013) && (MONTH >= 9)) {
            stawka = 1.0;
            czasPonad = ((czasPrzed + HOUR - 1) / HOUR) * HOUR;
            czasPonad += ((czasPo + HOUR - 1) / HOUR) * HOUR;
        }

        cols.put(CZASPONAD, czasPonad);
        stawka = BPMath.roundCurrency(stawka * dziecko.getRabat1AsFactor());
        cols.put(CENAGODZ, stawka);

        double opieka = ((czasPonad - 1) / HOUR + 1) * stawka;
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
