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
import name.prokop.bart.gae.edziecko.util.BPMath;

public class ParametryRozliczenia implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8306940386289808757L;
    private double stawkaZywieniowa = 3.80;

    /**
     * @return the stawkaZywieniowa
     */
    public double getStawkaZywieniowa() {
        return stawkaZywieniowa;
    }

    /**
     * @param stawkaZywieniowa the stawkaZywieniowa to set
     */
    public void setStawkaZywieniowa(double stawkaZywieniowa) {
        stawkaZywieniowa = BPMath.roundCurrency(stawkaZywieniowa);
        this.stawkaZywieniowa = stawkaZywieniowa;
    }
}
