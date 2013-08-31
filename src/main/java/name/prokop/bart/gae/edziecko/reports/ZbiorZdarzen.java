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

import com.google.appengine.api.datastore.Key;
import java.util.*;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.bol.cacheable.CacheableZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

public class ZbiorZdarzen {

    private final Przedszkole przedszkole;
    private final Dziecko dziecko;
    private final Date from, to;
    private final List<Zdarzenie> zdarzenia = new ArrayList<Zdarzenie>();

    public static String buildID(Key pKey, int okres) {
        return "ZZ_" + pKey.getId() + "_" + okres;
    }

    public static ZbiorZdarzen getZbiorZdarzen(EDzieckoRequest request) {
        String key = buildID(request.getPrzedszkoleKey(), request.getRokMiesiac());
        CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
        if (czz != null) {
            return new ZbiorZdarzen(request.getPrzedszkoleKey(), request.decodeRokMiesiacFrom(), request.decodeRokMiesiacTo(), czz.decode());
        }
        ZbiorZdarzen zbiorZdarzen = new ZbiorZdarzen(request);
        IC.INSTANCE.store(key, zbiorZdarzen);
        return zbiorZdarzen;
    }

    private ZbiorZdarzen(EDzieckoRequest request) {
        this(request.getPrzedszkoleKey(), request.decodeRokMiesiacFrom(), request.decodeRokMiesiacTo());
    }

    public static ZbiorZdarzen getZbiorZdarzen(Key przedszkoleKey, Date date) {
        int rokMiesiac = Integer.parseInt(DateToolbox.getFormatedDate("yyyyMM", date));
        String key = buildID(przedszkoleKey, rokMiesiac);
        CacheableZbiorZdarzen czz = IC.INSTANCE.retrieveZbiorZdarzen(key);
        if (czz != null) {
            List<Zdarzenie> zz = czz.decode();
            Date from = DateToolbox.getBeginingOfDay(date);
            Date to = DateToolbox.getEndOfDay(date);
            Iterator<Zdarzenie> iterator = zz.iterator();
            while (iterator.hasNext()) {
                Zdarzenie next = iterator.next();
                if (next.getCzasZdarzenia().before(from) || next.getCzasZdarzenia().after(to)) {
                    iterator.remove();
                }
            }
            return new ZbiorZdarzen(przedszkoleKey, from, to, zz);
        }

        return new ZbiorZdarzen(przedszkoleKey, date);
    }

    private ZbiorZdarzen(Key przedszkoleKey, Date date) {
        this(przedszkoleKey, DateToolbox.getBeginingOfDay(date), DateToolbox.getEndOfDay(date));
    }

    public ZbiorZdarzen(String numerKarty, int rokMiesiac) {
        this(numerKarty, EDzieckoRequest.decodeRokMiesiacFrom(rokMiesiac), EDzieckoRequest.decodeRokMiesiacTo(rokMiesiac));
    }

    @SuppressWarnings("unchecked")
    private ZbiorZdarzen(Key przedszkoleKey, Date from, Date to) {
        this.dziecko = null;
        this.from = from;
        this.to = to;
        PersistenceManager pm = PMF.getPM();
        Query q = pm.newQuery(Zdarzenie.class);
        q.setFilter("(przedszkoleKey==przedszkoleKeyParam) && (czasZdarzenia>fromParam) && (czasZdarzenia<toParam)");
        q.setOrdering("czasZdarzenia");
        q.declareParameters(Key.class.getName() + " przedszkoleKeyParam, " + "java.util.Date fromParam, " + "java.util.Date toParam");
        try {
            przedszkole = IC.INSTANCE.findPrzedszkole(przedszkoleKey);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("przedszkoleKeyParam", przedszkoleKey);
            params.put("fromParam", from);
            params.put("toParam", to);
            zdarzenia.addAll((List<Zdarzenie>) q.executeWithMap(params));
        } finally {
            q.closeAll();
            pm.close();
        }
    }

    @SuppressWarnings("unchecked")
    private ZbiorZdarzen(String numerKarty, Date from, Date to) {
        Karta karta = IC.INSTANCE.getKartaByCN(numerKarty);
        if (karta == null) {
            throw new IllegalArgumentException("Nie znaleziono karty o numerze");
        }

        this.from = from;
        this.to = to;
        PersistenceManager pm = PMF.getPM();
        Query q = pm.newQuery(Zdarzenie.class);
        q.setFilter("(dzieckoKey==dzieckoKeyParam) && (czasZdarzenia>fromParam) && (czasZdarzenia<toParam)");
        q.setOrdering("czasZdarzenia");
        q.declareParameters(Key.class.getName() + " dzieckoKeyParam, " + "java.util.Date fromParam, " + "java.util.Date toParam");
        try {
            dziecko = karta.getDziecko();
            dziecko.getImieNazwisko();
            przedszkole = dziecko.getPrzedszkole();
            przedszkole.getNazwa(); // fetch data ;)
            for (Karta k : dziecko.getKarty()) {
                k.getNumerKarty(); // fetch data ;)
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("dzieckoKeyParam", dziecko.getKey());
            params.put("fromParam", from);
            params.put("toParam", to);
            zdarzenia.addAll((List<Zdarzenie>) q.executeWithMap(params));
        } finally {
            q.closeAll();
        }
        pm.close();
    }

    private ZbiorZdarzen(Key przedszkoleKey, Date from, Date to, List<Zdarzenie> zdarzenia) {
        przedszkole = IC.INSTANCE.findPrzedszkole(przedszkoleKey);
        dziecko = null;
        this.from = from;
        this.to = to;
        this.zdarzenia.addAll(zdarzenia);
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Przedszkole getPrzedszkole() {
        return przedszkole;
    }

    public Dziecko getDziecko() {
        return dziecko;
    }

    public List<Zdarzenie> getZdarzenia() {
        return zdarzenia;
    }

    @Override
    public String toString() {
        int counter1 = 0;
        int counter2;
        StringBuilder sb = new StringBuilder();

        sb.append(przedszkole.getNazwa()).append('\n');
        sb.append(przedszkole.getAdres().getAddress()).append('\n');
        sb.append("NIP: ").append(przedszkole.getNip()).append('\n');

        sb.append('\n').append("Dzieci: ").append(przedszkole.getDzieci().size()).append('\n');
        for (Dziecko d : przedszkole.getDzieci()) {
            sb.append(++counter1).append(". ").append(d).append('\n');
            counter2 = 0;
            for (Karta k : d.getKarty()) {
                sb.append((char) (counter2++ + 'a')).append(". ").append(k).append('\n');
            }
        }

        sb.append('\n').append("zdarzenia: ").append(zdarzenia.size()).append('\n');
//		counter1 = 0;
//		Zdarzenie p = null;
//		for (Zdarzenie z : zdarzenia) {
//			sb.append(++counter1);
//			sb.append(z);
//			if (z.equals(p)) {
//				sb.append(" *** DUPLIKAT");
//				IC.getInstance().getZdarzeniaDoUsuniecia().add(z);
//			}
//			sb.append('\n');
//			p = z;
//		}
        return sb.toString();
    }
}
