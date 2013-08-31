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
import java.util.Date;
import name.prokop.bart.gae.edziecko.util.BPMath;

public class Wplata implements Serializable, Comparable<Wplata> {

    private static final long serialVersionUID = 4719722817798652629L;
    private Long dzieckoKey;
    private Date dzienZaplaty;
    private double opieka;
    private double zywienie;

    /**
     * @return the dzieckoKey
     */
    public Long getDzieckoKey() {
        return dzieckoKey;
    }

    /**
     * @param dzieckoKey the dzieckoKey to set
     */
    public void setDzieckoKey(Long dzieckoKey) {
        this.dzieckoKey = dzieckoKey;
    }

    /**
     * @return the dzienZaplaty
     */
    public Date getDzienZaplaty() {
        return dzienZaplaty;
    }

    /**
     * @param dzienZaplaty the dzienZaplaty to set
     */
    public void setDzienZaplaty(Date dzienZaplaty) {
        this.dzienZaplaty = dzienZaplaty;
    }

    /**
     * @return the opieka
     */
    public double getOpieka() {
        return opieka;
    }

    /**
     * @param opieka the opieka to set
     */
    public void setOpieka(double opieka) {
        opieka = BPMath.roundCurrency(opieka);
        this.opieka = opieka;
    }

    public double getOpiekaWplata() {
        return opieka > 0.0 ? opieka : 0.0;
    }

    public double getOpiekaWyplata() {
        return opieka < 0.0 ? -opieka : 0.0;
    }

    /**
     * @return the zywienie
     */
    public double getZywienie() {
        return zywienie;
    }

    /**
     * @param zywienie the zywienie to set
     */
    public void setZywienie(double zywienie) {
        zywienie = BPMath.roundCurrency(zywienie);
        this.zywienie = zywienie;
    }

    public double getZywienieWplata() {
        return zywienie > 0.0 ? zywienie : 0.0;
    }

    public double getZywienieWyplata() {
        return zywienie < 0.0 ? -zywienie : 0.0;
    }

    @Override
    public int compareTo(Wplata o) {
        return this.getDzienZaplaty().compareTo(o.getDzienZaplaty());
    }
}
