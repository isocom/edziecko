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
package name.prokop.bart.gae.edziecko.bol.blobs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import name.prokop.bart.gae.edziecko.util.BPMath;

public class BilansOtwarcia implements Serializable {

    private static final long serialVersionUID = 1124972830198009562L;
    private Map<Long, Double> opieka = new HashMap<Long, Double>();
    private Map<Long, Double> zywienie = new HashMap<Long, Double>();

    /**
     * @return the opieka
     */
    public double getOpieka(long key) {
        return opieka.get(key) == null ? 0.0 : opieka.get(key);
    }

    public double getOpiekaNadplata(long key) {
        double o_bo = getOpieka(key);
        return (o_bo > 0.0 ? o_bo : 0.0);
    }

    public double getOpiekaZaleglosc(long key) {
        double o_bo = getOpieka(key);
        return (o_bo < 0.0 ? -o_bo : 0.0);
    }

    /**
     *
     * @param key
     * @param val
     */
    public void setOpieka(long key, double val) {
        val = BPMath.roundCurrency(val);
        opieka.put(key, val);
    }

    /**
     * @return the zywienie
     */
    public double getZywienie(long key) {
        return zywienie.get(key) == null ? 0.0 : zywienie.get(key);
    }

    public double getZywienieNadplata(long key) {
        double z_bo = getZywienie(key);
        return (z_bo > 0.0 ? z_bo : 0.0);
    }

    public double getZywienieZaleglosc(long key) {
        double z_bo = getZywienie(key);
        return (z_bo < 0 ? -z_bo : 0.0);
    }

    /**
     * @param zywienie the zywienie to set
     */
    public void setZywienie(long key, double val) {
        val = BPMath.roundCurrency(val);
        zywienie.put(key, val);
    }

    public double sumOpieka() {
        double retVal = 0.0;
        for (double d : opieka.values()) {
            retVal += d;
        }
        return retVal;
    }

    public double sumZywienie() {
        double retVal = 0.0;
        for (double d : zywienie.values()) {
            retVal += d;
        }
        return retVal;
    }

    public double sumNadplataOpieka() {
        double retVal = 0.0;
        for (double d : opieka.values()) {
            if (d > 0.0) {
                retVal += d;
            }
        }
        return retVal;
    }

    public double sumNadplataZywienie() {
        double retVal = 0.0;
        for (double d : zywienie.values()) {
            if (d > 0.0) {
                retVal += d;
            }
        }
        return retVal;
    }

    public double sumZalegloscOpieka() {
        double retVal = 0.0;
        for (double d : opieka.values()) {
            if (d < 0.0) {
                retVal += -d;
            }
        }
        return retVal;
    }

    public double sumZalegloscZywienie() {
        double retVal = 0.0;
        for (double d : zywienie.values()) {
            if (d < 0.0) {
                retVal += -d;
            }
        }
        return retVal;
    }
}
