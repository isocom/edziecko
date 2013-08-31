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
import java.io.Serializable;
import java.util.Date;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import name.prokop.bart.gae.edziecko.util.DateToolbox;

@PersistenceCapable
public class Zdarzenie implements Serializable, Comparable<Zdarzenie>, LogicEntity {

    private static final long serialVersionUID = -552179709517888355L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    /**
     * Którego przedszkola dotyczy zdarzenie
     */
    @Persistent
    private Key przedszkoleKey;
    /**
     * Którego dziecka dotyczy zdarzenie
     */
    @Persistent
    private Key dzieckoKey;
    /**
     * Której karty dotyczy zdarzenie
     */
    @Persistent
    private Key kartaKey;
    /**
     * Data i czas zdarzenia
     */
    @Persistent
    private Date czasZdarzenia = null;
    /**
     * Jakiego rodzaju zdarzenie
     */
    @Persistent
    private TypZdarzenia typZdarzenia;

    /**
     * @return the key
     */
    @Override
    public Key getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * @return the przedszkoleKey
     */
    public Key getPrzedszkoleKey() {
        return przedszkoleKey;
    }

    /**
     * @param przedszkoleKey the przedszkoleKey to set
     */
    public void setPrzedszkoleKey(Key przedszkoleKey) {
        this.przedszkoleKey = przedszkoleKey;
    }

    /**
     * @return the dzieckoKey
     */
    public Key getDzieckoKey() {
        return dzieckoKey;
    }

    /**
     * @param dzieckoKey the dzieckoKey to set
     */
    public void setDzieckoKey(Key dzieckoKey) {
        this.dzieckoKey = dzieckoKey;
    }

    /**
     * @return the kartaKey
     */
    public Key getKartaKey() {
        return kartaKey;
    }

    /**
     * @param kartaKey the kartaKey to set
     */
    public void setKartaKey(Key kartaKey) {
        this.kartaKey = kartaKey;
    }

    /**
     * @return the czasZdarzenia
     */
    public Date getCzasZdarzenia() {
        return czasZdarzenia;
    }

    /**
     * @param czasZdarzenia the czasZdarzenia to set
     */
    public void setCzasZdarzenia(Date czasZdarzenia) {
        this.czasZdarzenia = czasZdarzenia;
    }

    /**
     * @return the typZdarzenia
     */
    public TypZdarzenia getTypZdarzenia() {
        return typZdarzenia;
    }

    /**
     * @param typZdarzenia the typZdarzenia to set
     */
    public void setTypZdarzenia(TypZdarzenia typZdarzenia) {
        this.typZdarzenia = typZdarzenia;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(": ");
        sb.append(getTypZdarzenia());
        sb.append(", ");
        sb.append(DateToolbox.getFormatedDate("yyyy-MM-dd HH:mm:ss", getCzasZdarzenia()));
        sb.append(", ");
        sb.append(getKartaKey());
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Zdarzenie)) {
            return false;
        }

        Zdarzenie z = (Zdarzenie) obj;
        if (key != null && z.key != null && key.equals(z.key)) {
            return true;
        }
        if (key != null && z.key != null && czasZdarzenia.equals(z.czasZdarzenia) && kartaKey.equals(z.kartaKey) && typZdarzenia.equals(z.typZdarzenia)) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(Zdarzenie o) {
        if (kartaKey == null || o.kartaKey == null) {
            return czasZdarzenia.compareTo(o.czasZdarzenia);
        } else {
            int c = czasZdarzenia.compareTo(o.czasZdarzenia);
            if (c < 0) {
                return -2;
            } else if (c == 0) {
                int k = kartaKey.compareTo(o.kartaKey);
                if (k < 0) {
                    return -1;
                } else if (k == 0) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 2;
            }
        }
    }

    @Override
    public void persist(PersistenceManager pm) {
        if (key != null) {
            throw new IllegalStateException("key != null " + Przedszkole.class);
        }
        if (przedszkoleKey == null) {
            throw new IllegalArgumentException("przedszkoleKey == null");
        }
        if (dzieckoKey == null) {
            throw new IllegalArgumentException("dzieckoKey == null");
        }
        if (kartaKey == null) {
            throw new IllegalArgumentException("kartaKey == null");
        }
        if (czasZdarzenia == null) {
            throw new IllegalArgumentException("czasZdarzenia == null");
        }
        if (typZdarzenia == null) {
            throw new IllegalArgumentException("typZdarzenia == null");
        }
        pm.makePersistent(this);
    }
}
