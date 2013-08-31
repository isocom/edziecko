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

public class Trzciana extends DzienPobytuDziecka {

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
    private final Date TBEGF;
    private final Date TENDF;
    private final Date TBEGD;
    private final Date TMIDD;
    private final Date TENDD;
    // private final Calendar calendar;
    private final Map<String, Object> cols = new HashMap<String, Object>();

    public Trzciana(KidsReport raport, Dziecko dziecko, Date date, NavigableSet<Zdarzenie> entries, Map<String, Object> shared) {
        super(raport, dziecko, date, entries, shared);

        TBEGD = DateToolbox.getBeginingOfDay(date, 06 * HOUR + 30 * MIN);
        TMIDD = DateToolbox.getBeginingOfDay(date, 10 * HOUR + 15 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 00 * MIN);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        calendar.setTime(date);

        TBEGF = DateToolbox.getBeginingOfDay(date, 8 * HOUR + 00 * MIN);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY) {
            TENDF = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 30 * MIN);
        } else {
            TENDF = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 00 * MIN);
        }

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

        if (czasDo.before(TMIDD) && czasPobytu < 60 * 15) {
            czasDo = TENDD;
            uwagi = "Brak wieczora (czas pobytu < 15 min)";
        }
        if (czasOd.after(TMIDD) && czasPobytu < 60 * 15) {
            czasOd = TBEGD;
            uwagi = "Brak poranku (czas pobytu < 15 minut)";
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
        double opieka = ((czasPo + czasPrzed - 1) / SEC / HOUR + 1) * 2;
        if ((czasPo + czasPrzed) == 0) {
            opieka = 0.0;
        }

        double zywienie = 3.0;
        opieka = BPMath.roundCurrency(opieka * dziecko.getRabat1AsFactor());
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
