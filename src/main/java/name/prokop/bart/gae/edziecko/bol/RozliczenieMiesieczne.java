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
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import name.prokop.bart.gae.edziecko.bol.blobs.*;
import name.prokop.bart.gae.edziecko.util.BPMath;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;

/**
 *
 * @author Bartłomiej P. Prokop
 */
@PersistenceCapable
public class RozliczenieMiesieczne implements Serializable, LogicEntity {

    public static String buildID(Key pKey, int okres) {
        return "RM_" + pKey.getId() + "_" + okres;
    }
    private static final long serialVersionUID = -3479696580075556139L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    /**
     * Którego przedszkola dotyczy rozliczenie okresowe
     */
    @Persistent
    private Key przedszkoleKey;
    /**
     * Wyznacznik okresu. Np. 201011 201112 201201 201202 YYYYMM
     */
    @Persistent
    private int rokMiesiac;
    @Persistent(serialized = "true")
    private ParametryRozliczenia parametry = new ParametryRozliczenia();
    @Persistent(serialized = "true")
    private BilansOtwarcia bilansOtwarcia = new BilansOtwarcia();
    @Persistent(serialized = "true")
    private Wplaty wplaty = new Wplaty();
    @Persistent(serialized = "true")
    private Zaliczki zaliczki = new Zaliczki();
    @Persistent(serialized = "true")
    private Zuzycie zuzycie = new Zuzycie();

    //TODO: move it to tools
    public Set<Key> getListaDzieci() {
        final Przedszkole przedszkole = IC.INSTANCE.findPrzedszkole(przedszkoleKey);
        Set<Key> retVal = new TreeSet<Key>(new Comparator<Key>() {

            @Override
            public int compare(Key k1, Key k2) {
                return Dziecko.compare(przedszkole.getDziecko(k1), przedszkole.getDziecko(k2));
            }
        });

        for (Dziecko d : przedszkole.getDzieci()) {
            retVal.add(d.getKey());
        }
        return retVal;
    }

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
     * @return the rokMiesiac
     */
    public int getRokMiesiac() {
        return rokMiesiac;
    }

    /**
     * @param rokMiesiac the rokMiesiac to set
     */
    public void setRokMiesiac(int rokMiesiac) {
        this.rokMiesiac = rokMiesiac;
    }

    /**
     * @return the bilansOtwarcia
     */
    public BilansOtwarcia getBilansOtwarcia() {
        return bilansOtwarcia;
    }

    /**
     * @param bilansOtwarcia the bilansOtwarcia to set
     */
    public void setBilansOtwarcia(BilansOtwarcia bilansOtwarcia) {
        this.bilansOtwarcia = bilansOtwarcia;
    }

    /**
     * @return the wplaty
     */
    public Wplaty getWplaty() {
        return wplaty;
    }

    /**
     * @param wplaty the wplaty to set
     */
    public void setWplaty(Wplaty wplaty) {
        this.wplaty = wplaty;
    }

    /**
     * @return the zaliczki
     */
    public Zaliczki getZaliczki() {
        return zaliczki;
    }

    /**
     * @param zaliczki the zaliczki to set
     */
    public void setZaliczki(Zaliczki zaliczki) {
        this.zaliczki = zaliczki;
    }

    /**
     * @return the zuzycie
     */
    public Zuzycie getZuzycie() {
        return zuzycie;
    }

    /**
     * @param zuzycie the zuzycie to set
     */
    public void setZuzycie(Zuzycie zuzycie) {
        this.zuzycie = zuzycie;
    }

    /**
     * @return the parametry
     */
    public ParametryRozliczenia getParametry() {
        return parametry;
    }

    /**
     * @param parametry the parametry to set
     */
    public void setParametry(ParametryRozliczenia parametry) {
        this.parametry = parametry;
    }

    @Override
    public void persist(PersistenceManager pm) {
        if (key != null) {
            throw new IllegalStateException("key != null " + Przedszkole.class);
        }
        if (rokMiesiac == 0) {
            throw new IllegalArgumentException("nazwa == null " + Przedszkole.class);
        }
        pm.makePersistent(this);
    }

    public double getSaldoOpieka(long id) {
        double saldo = bilansOtwarcia.getOpieka(id);
        saldo -= zuzycie.getOpieka(id);
        saldo += wplaty.liczWplateSumaryczna(id).getOpieka();
        return saldo;
    }

    public double getSaldoZywienie(long id) {
        double saldo = bilansOtwarcia.getZywienie(id);
        saldo -= zuzycie.getZywienie(id);
        saldo += wplaty.liczWplateSumaryczna(id).getZywienie();
        return saldo;
    }

    public double getPrzypisOpieka(long id) {
        return zaliczki.getOpieka(id);
    }

    public double getOdpisOpieka(long id) {
        return zaliczki.getOpieka(id) - zuzycie.getOpieka(id);
    }

    public int getDniPrzypisOpieka(long id) {
        return zaliczki.getDniOpieka(id);
    }

    public int getDniOdpisOpieka(long id) {
        return zaliczki.getDniOpieka(id) - zuzycie.getDniOpieka(id);
    }

    public double getPrzypisZywienie(long id) {
        return zaliczki.getZywienie(id);
    }

    public double getOdpisZywienie(long id) {
        return zaliczki.getZywienie(id) - zuzycie.getZywienie(id);
    }

    public int getDniPrzypisZywienie(long id) {
        return zaliczki.getDniZywienie(id);
    }

    public int getDniOdpisZywienie(long id) {
        return zaliczki.getDniZywienie(id) - zuzycie.getDniZywienie(id);
    }

    public double getDoZaplatyRodzic1(long id) {
        double retVal = 0.0;
        retVal += bilansOtwarcia.getOpieka(id);
        retVal += bilansOtwarcia.getZywienie(id);
        retVal = -retVal;
        retVal += zaliczki.getOpieka(id);
        retVal += zaliczki.getZywienie(id);
        return BPMath.roundCurrency(retVal);
    }

    public double opiekaDoZaplaty(long key) {
        return getBilansOtwarcia().getOpieka(key) - getZaliczki().getOpieka(key);
    }

    public double zywienieDoZaplaty(long key) {
        return getBilansOtwarcia().getZywienie(key) - getZaliczki().getZywienie(key);
    }

    public Date decodeRokMiesiacFrom() {
        return EDzieckoRequest.decodeRokMiesiacFrom(getRokMiesiac());
    }

    public Date decodeRokMiesiacTo() {
        return EDzieckoRequest.decodeRokMiesiacTo(getRokMiesiac());
    }
}
