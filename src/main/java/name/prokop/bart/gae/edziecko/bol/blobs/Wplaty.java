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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Wplaty implements Serializable {

    private static final long serialVersionUID = -6852326026367842171L;
    private List<Wplata> platnosci = new ArrayList<Wplata>();

    public Wplata liczWplateSumaryczna(long dzieckoKey) {
        Wplata retVal = new Wplata();
        for (Wplata w : platnosci) {
            if (dzieckoKey == w.getDzieckoKey().longValue()) {
                retVal.setOpieka(retVal.getOpieka() + w.getOpieka());
                retVal.setZywienie(retVal.getZywienie() + w.getZywienie());
            }
        }
        return retVal;
    }

    public double sum() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getOpieka();
            retVal += w.getZywienie();
        }
        return retVal;
    }

    public double sumOpieka() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getOpieka();
        }
        return retVal;
    }

    public double sumOpiekaWplata() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getOpiekaWplata();
        }
        return retVal;
    }

    public double sumOpiekaWyplata() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getOpiekaWyplata();
        }
        return retVal;
    }

    public double sumZywienie() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getZywienie();
        }
        return retVal;
    }

    public double sumZywienieWplata() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getZywienieWplata();
        }
        return retVal;
    }

    public double sumZywienieWyplata() {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            retVal += w.getZywienieWyplata();
        }
        return retVal;
    }

    public double liczDziennaOpieka(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getOpieka();
            }
        }
        return retVal;
    }

    public double liczDziennaOpiekaWplata(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getOpiekaWplata();
            }
        }
        return retVal;
    }

    public double liczDziennaOpiekaWyplata(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getOpiekaWyplata();
            }
        }
        return retVal;
    }

    public double liczDziennaZywienie(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getZywienie();
            }
        }
        return retVal;
    }

    public double liczDziennaZywienieWplata(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getZywienieWplata();
            }
        }
        return retVal;
    }

    public double liczDziennaZywienieWyplata(Date date) {
        double retVal = 0.0;
        for (Wplata w : platnosci) {
            if (date.equals(w.getDzienZaplaty())) {
                retVal += w.getZywienieWyplata();
            }
        }
        return retVal;
    }

    public double liczDzienna(Date date) {
        return liczDziennaOpieka(date) + liczDziennaZywienie(date);
    }

    /**
     * @return the platnosci
     */
    public List<Wplata> getPlatnosci() {
        return platnosci;
    }

    /**
     * @param platnosci the platnosci to set
     */
    public void setPlatnosci(List<Wplata> platnosci) {
        this.platnosci = platnosci;
    }

    public void sortuj() {
        Collections.sort(getPlatnosci());
    }
}
