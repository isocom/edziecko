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
package name.prokop.bart.gae.edziecko.bol;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PostalAddress;
import java.io.Serializable;
import java.util.*;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.*;

/**
 * Informacja o przedszkolu.
 *
 * @author bart
 */
@PersistenceCapable
public class Przedszkole implements Serializable, LogicEntity {

    private static final long serialVersionUID = 3656236934214832237L;
    @NotPersistent
    private transient Map<String, Karta> cache_kartyBySN = null;
    @NotPersistent
    private transient Map<String, Karta> cache_kartyByCN = null;
    @NotPersistent
    private transient Map<Key, Dziecko> cache_dzieci = null;
    @NotPersistent
    private transient Map<Key, Karta> cache_karty = null;
    @NotPersistent
    private transient Map<Long, Dziecko> cache_dzieci2 = null;
    @NotPersistent
    private transient Map<Long, Karta> cache_karty2 = null;

    public Dziecko getDziecko(Key key) {
        return cache_dzieci.get(key);
    }

    public Dziecko getDziecko(Long key) {
        return cache_dzieci2.get(key);
    }

    public Karta getKarta(Key key) {
        return cache_karty.get(key);
    }

    public Karta getKarta(Long key) {
        return cache_karty2.get(key);
    }

    public Karta getKartaBySN(String sn) {
        return cache_kartyBySN.get(sn);
    }

    public Karta getKartaByCN(String cn) {
        return cache_kartyByCN.get(cn);
    }

    public void populateLocalCache() {
        cache_dzieci = new HashMap<Key, Dziecko>();
        cache_karty = new HashMap<Key, Karta>();
        cache_dzieci2 = new HashMap<Long, Dziecko>();
        cache_karty2 = new HashMap<Long, Karta>();
        cache_kartyByCN = new HashMap<String, Karta>();
        cache_kartyBySN = new HashMap<String, Karta>();

        for (Dziecko d : getDzieci()) {
            cache_dzieci.put(d.getKey(), d);
            cache_dzieci2.put(d.getKey().getId(), d);

            for (Karta k : d.getKarty()) {
                cache_karty.put(k.getKey(), k);
                cache_karty2.put(k.getKey().getId(), k);
                cache_kartyByCN.put(k.getNumerKarty(), k);
                cache_kartyBySN.put(k.getNumerSeryjny(), k);
            }
        }
    }
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    /**
     * Lista dzieci uczęszczających do przedszkola
     */
    @Persistent(mappedBy = "przedszkole")
    @Element(dependent = "true")
    private Set<Dziecko> dzieci = new HashSet<Dziecko>();
//    private List<Dziecko> dzieci = new ArrayList<Dziecko>();
    /**
     * Nazwa przedszkola
     */
    @Persistent
    private String nazwa;
    /**
     * Adres przedszkola
     */
    @Persistent
    private PostalAddress adres;
    /**
     * Numer NIP. Wpisujemy bez kresek;
     */
    @Persistent
    private String nip;
    /**
     * Nazwa klasy wykonującej obliczenia dotyczące dnia pobytu dziecka w
     * przedszkolu
     */
    @Persistent
    private String kalkulatorDnia;
    /**
     * Czas, kiedy wygasa licencja
     */
    @Persistent
    private Date terminLicencji;

    /**
     * Dodaje dziecko do danego przeszkola
     *
     * @param dziecko
     */
    public void add(Dziecko dziecko) {
        getDzieci().add(dziecko);
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public Key getKey() {
        return key;
    }

    public void setDzieci(Set<Dziecko> dzieci) {
        this.dzieci = dzieci;
    }

    public Set<Dziecko> getDzieci() {
        return dzieci;
    }

    public List<Dziecko> getDzieciPosortowane() {
        List<Dziecko> retVal = new ArrayList<Dziecko>();
        retVal.addAll(dzieci);
        Collections.sort(retVal);
        return retVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Przedszkole - klucz: ").append(key).append('\n');
        sb.append("Nazwa: ").append(getNazwa()).append('\n');
        sb.append("Addres: ").append(getAdres() != null ? getAdres().getAddress() : "--- BRAK ADRESU ---").append('\n');
        sb.append("NIP: ").append(getNip()).append('\n');
        sb.append("Ważność licencji: ").append(getTerminLicencji()).append('\n');
        sb.append("Kalkulator dnia: ").append(getKalkulatorDnia()).append('\n');
        return sb.toString();
    }

    @Override
    public void persist(PersistenceManager pm) {
        if (key != null) {
            throw new IllegalStateException("key != null " + Przedszkole.class);
        }
        if (nazwa == null) {
            throw new IllegalArgumentException("nazwa == null " + Przedszkole.class);
        }
        if (adres == null) {
            throw new IllegalArgumentException("adres == null " + Przedszkole.class);
        }
        if (nip == null) {
            throw new IllegalArgumentException("nip == null " + Przedszkole.class);
        }
        cleanObject();
        if (nazwa.isEmpty()) {
            throw new IllegalArgumentException("nazwa.isEmpty() " + Przedszkole.class);
        }
        if (nip.isEmpty()) {
            throw new IllegalArgumentException("nip.isEmpty() " + Przedszkole.class);
        }

        Query q = pm.newQuery(Przedszkole.class, "nip == nowyNip");
        q.declareParameters("String nowyNip");
        @SuppressWarnings("unchecked")
        List<Przedszkole> results = (List<Przedszkole>) q.execute(this.nip);
        if (results.isEmpty()) {
            pm.makePersistent(this);
        } else {
            throw new IllegalArgumentException("not unique " + Przedszkole.class);
        }
    }

    private void cleanObject() {
        nazwa = nazwa.trim();
        String tmp = "";
        for (int i = 0; i < nip.length(); i++) {
            if (Character.isDigit(nip.charAt(i))) {
                tmp += nip.charAt(i);
            }
        }
        nip = tmp;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public PostalAddress getAdres() {
        return adres;
    }

    public void setAdres(PostalAddress adres) {
        this.adres = adres;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getKalkulatorDnia() {
        return kalkulatorDnia;
    }

    public void setKalkulatorDnia(String kalkulatorDnia) {
        this.kalkulatorDnia = kalkulatorDnia;
    }

    public Date getTerminLicencji() {
        return terminLicencji;
    }

    public void setTerminLicencji(Date terminLicencji) {
        this.terminLicencji = terminLicencji;
    }
}
