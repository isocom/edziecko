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

public class Zaliczki implements Serializable {

    private static final long serialVersionUID = 1585213800380490463L;
    private Map<Long, Integer> dniOpieka = new HashMap<Long, Integer>();
    private Map<Long, Integer> dniZywienie = new HashMap<Long, Integer>();
    private Map<Long, Double> opieka = new HashMap<Long, Double>();
    private Map<Long, Double> zywienie = new HashMap<Long, Double>();

    /**
     * @return the opieka
     */
    public double getOpieka(long key) {
        return opieka.get(key) == null ? 0.0 : opieka.get(key);
    }

    /**
     * @param opieka the opieka to set
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

    /**
     * @param zywienie the zywienie to set
     */
    public void setZywienie(long key, double val) {
        val = BPMath.roundCurrency(val);
        zywienie.put(key, val);
    }

    /**
     * @return the dniOpieka
     */
    public int getDniOpieka(long key) {
        return dniOpieka.get(key) == null ? 0 : dniOpieka.get(key);
    }

    /**
     * @param dniOpieka the dniOpieka to set
     */
    public void setDniOpieka(long key, int val) {
        dniOpieka.put(key, val);
    }

    /**
     * @return the dniZywienie
     */
    public int getDniZywienie(long key) {
        return dniZywienie.get(key) == null ? 0 : dniZywienie.get(key);
    }

    /**
     * @param dniZywienie the dniZywienie to set
     */
    public void setDniZywienie(long key, int val) {
        dniZywienie.put(key, val);
    }
}
