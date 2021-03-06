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

/**
 * @author Bartłomiej P. Prokop
 *
 */
public enum TypZdarzenia {

    /**
     * Przyłożenie karty do rejestratora
     */
    PrzylozenieKarty,
    /**
     * Ręczne dodanie zdarzenia - np. dziecko nie odbiło się
     */
    WpisManualny,
    /**
     * Wygenerowane zdarzenie automatycznie - np. domknięcie przez system
     * wyjścia
     */
    Tablet,
    KontrolaDostepu;

    @Override
    public String toString() {
        switch (this) {
            case PrzylozenieKarty:
                return "Przyłożono do czytnika";
            case WpisManualny:
                return "Dopisano ręcznie";
            case Tablet:
                return "Użyto tablet";
            case KontrolaDostepu:
                return "Kontrola dostepu";
            default:
                throw new IllegalStateException();
        }
    }
}
