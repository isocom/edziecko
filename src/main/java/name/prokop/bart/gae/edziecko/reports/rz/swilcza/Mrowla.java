package name.prokop.bart.gae.edziecko.reports.rz.swilcza;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

public class Mrowla extends DzienPobytuDziecka {

    private final static List<String> COLUMNS = new ArrayList<String>();
    private final static Map<String, TypKolumny> COLTYPE = new HashMap<String, TypKolumny>();
    private final static String CZAS_PORANNY = "Cena poranna";
    private final static String CZAS_WIECZOR = "Cena wieczorna";

    static {
        COLUMNS.add(CZAS_PORANNY);
        COLUMNS.add(CZAS_WIECZOR);
        COLTYPE.put(CZAS_PORANNY, TypKolumny.Kwota);
        COLTYPE.put(CZAS_WIECZOR, TypKolumny.Kwota);
    }
    private final Date T0651;
    private final Date T0745;
    private final Date T1321;
    private final Date T1416;
    private final Date T1516;
    private final Date T1616;
    private final Date TMIDD;
    private final Date TBEGD;
    private final Date TENDD;
    private final Map<String, Object> cols = new HashMap<String, Object>();

    public Mrowla(KidsReport raport, Dziecko dziecko, Date date, NavigableSet<Zdarzenie> entries, Map<String, Object> shared) {
        super(raport, dziecko, date, entries, shared);

        T0651 = DateToolbox.getBeginingOfDay(date, 06 * HOUR + 51 * MIN);
        T0745 = DateToolbox.getBeginingOfDay(date, 07 * HOUR + 45 * MIN);
        T1321 = DateToolbox.getBeginingOfDay(date, 13 * HOUR + 21 * MIN);
        T1416 = DateToolbox.getBeginingOfDay(date, 14 * HOUR + 16 * MIN);
        T1516 = DateToolbox.getBeginingOfDay(date, 15 * HOUR + 16 * MIN);
        T1616 = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 16 * MIN);

        TMIDD = DateToolbox.getBeginingOfDay(date, 10 * HOUR + 30 * MIN);

        TBEGD = DateToolbox.getBeginingOfDay(date, 06 * HOUR + 30 * MIN);
        TENDD = DateToolbox.getBeginingOfDay(date, 16 * HOUR + 30 * MIN);

        calc();
    }

    private void calc() {
        if (czasOd.equals(czasDo)) {
            if (czasOd.before(TMIDD)) {
                czasDo = TENDD;
                uwagi = "Brak wieczora (ustalono 16:30)";
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

        double pricePre = 0.0;
        double pricePost = 0.0;

        if (czasOd.getTime() < T0651.getTime()) {
            pricePre = 1.5;
        }
        if (T0651.getTime() <= czasOd.getTime() && czasOd.getTime() < T0745.getTime()) {
            pricePre = 1.0;
        }

        if (T1321.getTime() <= czasDo.getTime() && czasDo.getTime() < T1416.getTime()) {
            pricePost = 1.0;
        }
        if (T1416.getTime() <= czasDo.getTime() && czasDo.getTime() < T1516.getTime()) {
            pricePost = 2.0;
        }
        if (T1516.getTime() <= czasDo.getTime() && czasDo.getTime() < T1616.getTime()) {
            pricePost = 3.0;
        }
        if (T1616.getTime() <= czasDo.getTime()) {
            pricePost = 4.0;
        }

        pricePre = BPMath.roundCurrency(pricePre * dziecko.getRabat1AsFactor());
        pricePost = BPMath.roundCurrency(pricePost * dziecko.getRabat1AsFactor());

        cenaPobytu = BPMath.roundCurrency(pricePre + pricePost);
        cols.put(CZAS_PORANNY, pricePre);
        cols.put(CZAS_WIECZOR, pricePost);
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
