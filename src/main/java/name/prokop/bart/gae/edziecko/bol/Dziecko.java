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

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Key;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.*;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

@PersistenceCapable
public class Dziecko implements Serializable, Comparable<Dziecko> {

    private static final long serialVersionUID = 4457165252153253775L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    /**
     * Przedszkole do którego uczęszcza dziecko
     */
    @Persistent
    private Przedszkole przedszkole;
    /**
     * Czy dziecko uczeszcza do przedszkola (false = nie ladowac do zestawiec)
     */
    @Persistent
    private boolean aktywne = true;
    /**
     * Karty identyfikujące dziecko
     */
    @Persistent(mappedBy = "dziecko")
    @Element(dependent = "true")
    private List<Karta> karty = new ArrayList<Karta>();
    /**
     * Pesel dziecka (jego znajomość umożliwia przypisanie przez rodzica konta
     * Google do dziecka i oglądanie w internecie co się działo)
     */
    @Persistent
    private String pesel;
    @Persistent
    private String imieNazwisko;
    @Persistent
    private Category grupa;
    @Persistent
    private Double rabat1; // = 0.0;
    @Persistent
    private Double rabat2; // = 0.0;
    @Persistent
    private String pin;

    public Dziecko() {
    }

    public Dziecko(String imieNazwisko) {
        this.imieNazwisko = imieNazwisko;
    }

    public void add(Karta karta) {
        getKarty().add(karta);
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setPrzedszkole(Przedszkole przedszkole) {
        this.przedszkole = przedszkole;
    }

    public Przedszkole getPrzedszkole() {
        return przedszkole;
    }

    public void setKarty(List<Karta> karty) {
        this.karty = karty;
    }

    public List<Karta> getKarty() {
        return karty;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getImieNazwisko() {
        return imieNazwisko;
    }

    public void setImieNazwisko(String imieNazwisko) {
        this.imieNazwisko = imieNazwisko;
    }

    public boolean isAktywne() {
        return aktywne;
    }

    public void setAktywne(boolean aktywne) {
        this.aktywne = aktywne;
    }

    public Category getGrupa() {
        return grupa;
    }

    public void setGrupa(Category grupa) {
        this.grupa = grupa;
    }

    /**
     * @return the rabat1
     */
    public Double getRabat1() {
        return rabat1;
    }

    public double getRabat1AsFactor() {
        if (getRabat1() == null) {
            return 1.0;
        } else {
            return 1.0 - getRabat1();
        }
    }

    /**
     * @param rabat1 the rabat1 to set
     */
    public void setRabat1(Double rabat1) {
        this.rabat1 = rabat1;
    }

    /**
     * @return the rabat2
     */
    public Double getRabat2() {
        return rabat2;
    }

    public double getRabat2AsFactor() {
        if (getRabat2() == null) {
            return 1.0;
        } else {
            return 1.0 - getRabat2();
        }
    }

    /**
     * @param rabat2 the rabat2 to set
     */
    public void setRabat2(Double rabat2) {
        this.rabat2 = rabat2;
    }

    /**
     * @return the pin
     */
    public String getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return key + ": " + imieNazwisko + "/" + pesel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Dziecko)) {
            return false;
        }

        Dziecko d = (Dziecko) obj;
        if (key != null && d.key != null && key.equals(d.key)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(Dziecko o) {
        return compare(this, o);
    }

    public static int compare(Dziecko d1, Dziecko d2) {
        if (d1.equals(d2)) {
            return 0;
        }
        int c;

        if (d1.getGrupaAsString().equals("N/D") ^ d2.getGrupaAsString().equals("N/D")) {
            if (d1.getGrupaAsString().equals("N/D")) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        // compare by group name
        c = d1.getGrupaAsString().compareTo(d2.getGrupaAsString());
        if (c != 0) {
            return c;
        }

        // compare by name
        c = StringToolbox.compare(d1.imieNazwisko, d2.imieNazwisko);
        if (c != 0) {
            return c;
        }

        // last resort
        return d1.key.compareTo(d2.key);
    }

    public String getPeselAsString() {
        if (getPesel() != null) {
            return getPesel();
        } else {
            return "";
        }
    }

    public String getImieNazwiskoAsString() {
        if (getImieNazwisko() != null) {
            return getImieNazwisko();
        } else {
            return "";
        }
    }

    public String getGrupaAsString() {
        if (getGrupa() != null) {
            return getGrupa().getCategory();
        } else {
            return "";
        }
    }

    /**
     * @return the rabat1
     */
    public String getRabat1AsString() {
        if (getRabat1() != null) {
            return StringToolbox.d2r(getRabat1());
        } else {
            return "";
        }
    }

    /**
     * @return the rabat2
     */
    public String getRabat2AsString() {
        if (getRabat2() != null) {
            return StringToolbox.d2r(getRabat2());
        } else {
            return "";
        }
    }

    /**
     * @return the pin
     */
    public String getPinAsString() {
        if (getPin() != null) {
            return getPin();
        } else {
            return "";
        }
    }
}
