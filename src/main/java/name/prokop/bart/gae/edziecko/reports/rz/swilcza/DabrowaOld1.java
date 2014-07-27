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
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

public class Dabrowa extends DzienPobytuDziecka {

    private final static List<String> COLUMNS = new ArrayList<String>();
    private final static Map<String, TypKolumny> COLTYPE = new HashMap<String, TypKolumny>();
    private final static String CZASPRZED = "Czas przed";
    private final static String CZASPO = "Czas po";
    private final static String POBYT = "Opieka";
    private final static String ZYWIENIE = "Żywienie";

    static {
        COLUMNS.add(CZASPRZED);
        COLUMNS.add(CZASPO);
        COLUMNS.add(POBYT);
        COLUMNS.add(ZYWIENIE);
        COLTYPE.put(CZASPRZED, TypKolumny.OkresCzasu);
        COLTYPE.put(CZASPO, TypKolumny.OkresCzasu);
        COLTYPE.put(ZYWIENIE, TypKolumny.Kwota);
        COLTYPE.put(POBYT, TypKolumny.Kwota);
    }
    private final Date T_06_30;
    private final Date T_07_00;
    private final Date T_07_30;
    private final Date T_08_00;
    private final Date T_13_00;
    private final Date T_14_00;
    private final Date T_15_00;
    private final Date T_16_00;
    private final Date TBEGF;
    private final Date TENDF;
    private final Date TBEGD;
    private final Date TMIDD;
    private final Date TENDD;
    private final int dayOfWeek;
    private final Map<String, Object> cols = new HashMap<String, Object>();

    public Dabrowa(KidsReport raport, Dziecko dziecko, Date date, NavigableSet<Zdarzenie> entries, Map<String, Object> shared) {
        super(raport, dziecko, date, entries, shared);

        TMIDD = DateToolbox.getBeginingOfDay(date, 11 * HOUR + 15 * MIN);

        T_06_30 = DateToolbox.getBeginingOfDay(date, 6 * HOUR + 30 * MIN);
        T_07_00 = DateToolbox.getBeginingOfDay(date, 7 * HOUR + 00 * MIN);
        T_07_30 = DateToolbox.getBeginingOfDay(date, 7 * HOUR + 30 * MIN);
        T_08_00 = DateToolbox.getBeginingOfDay(date, 8 * HOUR + 00 * MIN);
        T_13_00 = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 00 * MIN);
        T_14_00 = DateToolbox.getBeginingOfDay(date, 14 * HOUR + 00 * MIN);
        T_15_00 = DateToolbox.getBeginingOfDay(date, 15 * HOUR + 00 * MIN);
        T_16_00 = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 00 * MIN);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        calendar.setTime(date);

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.FRIDAY) {
            TBEGF = DateToolbox.getBeginingOfDay(date, 7 * HOUR + 30 * MIN);
        } else {
            TBEGF = DateToolbox.getBeginingOfDay(date, 8 * HOUR + 00 * MIN);
        }
        TENDF = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 00 * MIN);
        TBEGD = DateToolbox.getBeginingOfDay(date, 6 * HOUR + 30 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 00 * MIN);
        calc();
    }

    private void calc() {
        if (czasOd.equals(czasDo)) {
            if (czasOd.before(TMIDD)) {
                czasDo = TENDD;
                uwagi = "Brak wieczora (ustalono 16:00)";
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

        int czasPrzed = (int) (TBEGF.getTime() / 1000 - czasOd.getTime() / 1000);
        int czasPo = (int) (czasDo.getTime() / 1000 - TENDF.getTime() / 1000);
        if (czasPrzed < 0) {
            czasPrzed = 0;
        }
        if (czasPo < 0) {
            czasPo = 0;
        }

        double opieka = 0.0;
        if (dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.FRIDAY) {
            if (czasOd.before(T_07_00)) {
                opieka += 0.5;
            }
            if (czasOd.before(T_07_30)) {
                opieka += 0.5;
            }
            if (czasDo.after(T_13_00)) {
                opieka += 1.0;
            }
            if (czasDo.after(T_14_00)) {
                opieka += 1.0;
            }
            if (czasDo.after(T_15_00)) {
                opieka += 1.0;
            }
        } else {
            if (czasOd.before(T_07_00)) {
                opieka += 0.5;
            }
            if (czasOd.before(T_07_30)) {
                opieka += 0.5;
            }
            if (czasOd.before(T_08_00)) {
                opieka += 0.5;
            }
            if (czasDo.after(T_13_00)) {
                opieka += 1.0;
            }
            if (czasDo.after(T_14_00)) {
                opieka += 1.0;
            }
            if (czasDo.after(T_15_00)) {
                opieka += 1.0;
            }
        }

        opieka = BPMath.roundCurrency(opieka * dziecko.getRabat1AsFactor());
        double zywienie = 3.0;
        zywienie = BPMath.roundCurrency(zywienie * dziecko.getRabat2AsFactor());

        cenaPobytu = BPMath.roundCurrency(zywienie + opieka);
        cols.put(CZASPRZED, czasPrzed);
        cols.put(CZASPO, czasPo);
        cols.put(POBYT, opieka);
        cols.put(ZYWIENIE, zywienie);
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
