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

import java.io.Serializable;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Karta implements Serializable {

    private static final long serialVersionUID = 7205585024724024681L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    @Persistent
    private Dziecko dziecko;
    /**
     * Sprzętowy numer seryjny karty MIFARE
     */
    @Persistent
    private String numerSeryjny;
    /**
     * Numer nadrukowany na karcie
     */
    @Persistent
    private String numerKarty;
    /**
     * Czy dana karta jest aktywna w systemie
     */
    @Persistent
    private boolean aktywna = true;
    @Persistent
    private String posiadacz;

    public Karta() {
    }

    public Karta(String numerSeryjny, String numerKarty) {
        this.numerKarty = numerKarty;
        this.numerSeryjny = numerSeryjny;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setDziecko(Dziecko dziecko) {
        this.dziecko = dziecko;
    }

    public Dziecko getDziecko() {
        return dziecko;
    }

    public String getNumerSeryjny() {
        return numerSeryjny;
    }

    public void setNumerSeryjny(String numerSeryjny) {
        this.numerSeryjny = numerSeryjny;
    }

    public String getNumerKarty() {
        return numerKarty;
    }

    public void setNumerKarty(String numerKarty) {
        this.numerKarty = numerKarty;
    }

    public boolean isAktywna() {
        return aktywna;
    }

    public void setAktywna(boolean aktywna) {
        this.aktywna = aktywna;
    }

    public String getPosiadacz() {
        return posiadacz;
    }

    public void setPosiadacz(String posiadacz) {
        this.posiadacz = posiadacz;
    }

    @Override
    public String toString() {
        return key + ": '" + numerSeryjny + "' == `" + numerKarty + "`";
    }

    public static Karta findByCN(PersistenceManager pm, String cn) {
        Query q = pm.newQuery(Karta.class);
        q.setFilter("numerKarty == numerKartyParam");
        q.declareParameters("String numerKartyParam");
        @SuppressWarnings("unchecked")
        List<Karta> karty = (List<Karta>) q.execute(cn);
        if (karty.size() == 1) {
            return karty.get(0);
        }
        if (karty.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("Zduplikowane karty " + cn + " - " + karty.size() + " szt.");
        }
    }
}
