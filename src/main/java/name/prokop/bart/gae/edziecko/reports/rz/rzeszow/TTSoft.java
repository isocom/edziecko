package name.prokop.bart.gae.edziecko.reports.rz.rzeszow;

import java.util.Date;
import java.util.Map;
import java.util.NavigableSet;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.DzienPobytuDziecka;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.TypKolumny;

public class TTSoft extends DzienPobytuDziecka {

	public TTSoft(KidsReport raport, Dziecko dziecko, Date data, NavigableSet<Zdarzenie> zdarzenia, Map<String, Object> shared) {
		super(raport, dziecko, data, zdarzenia, shared);
	}

	@Override
	public Map<String, Object> columnValues() {
		return null;
	}

	@Override
	public Map<String, TypKolumny> columnTypes() {
		return null;
	}

    @Override
    public int getRoundFactorOpieka() {
        return 0;
    }

    @Override
    public int getRoundFactorZywienie() {
        return 1;
    }

}
