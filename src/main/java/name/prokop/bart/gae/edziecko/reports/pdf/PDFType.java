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
package name.prokop.bart.gae.edziecko.reports.pdf;

import java.util.Date;
import name.prokop.bart.gae.edziecko.util.DateToolbox;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;

public enum PDFType {

    ListaObecnosci {

        @Override
        PDFReport getRenderer() {
            return new ListaObecnosci();
        }
    },
    Rozliczenie {

        @Override
        PDFReport getRenderer() {
            return new Rozliczenie();
        }
    },
    Raporty {

        @Override
        PDFReport getRenderer() {
            return new Raporty();
        }
    },
    BO {

        @Override
        PDFReport getRenderer() {
            return new BO();
        }
    },
    DumpPrzedszkole {

        @Override
        PDFReport getRenderer() {
            return new DumpPrzedszkole();
        }
    },
    Dzieci {

        @Override
        PDFReport getRenderer() {
            return new Dzieci();
        }
    },
    PaskiDlaRodzicow {

        @Override
        PDFReport getRenderer() {
            return new PaskiDlaRodzicow();
        }
    };

    abstract PDFReport getRenderer();

    public String buildName(EDzieckoRequest request) {
        switch (this) {
            case DumpPrzedszkole:
            case Dzieci:
                return name() + ".pdf";
            case BO:
            case Raporty:
            case Rozliczenie:
            case PaskiDlaRodzicow:
                return name() + "-" + request.getRokMiesiac() + ".pdf";
            case ListaObecnosci:
                return name() + "-" + DateToolbox.getFormatedDate("yyyyMMdd", (Date) request.getSession().getAttribute("data")) + ".pdf";
            default:
                return "Unknown.pdf";
        }
    }
}
