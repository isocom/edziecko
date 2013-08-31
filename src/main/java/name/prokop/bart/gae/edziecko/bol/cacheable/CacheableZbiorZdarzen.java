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
package name.prokop.bart.gae.edziecko.bol.cacheable;

import com.google.appengine.api.datastore.KeyFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.TypZdarzenia;
import name.prokop.bart.gae.edziecko.bol.Zdarzenie;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;
import name.prokop.bart.gae.edziecko.util.IC;

public class CacheableZbiorZdarzen implements Serializable {

    private final long przedszkoleKeyId;
    private final int size;
    private final byte[] data;

    public CacheableZbiorZdarzen(ZbiorZdarzen zbiorZdarzen) {
        przedszkoleKeyId = zbiorZdarzen.getPrzedszkole().getKey().getId();
        size = zbiorZdarzen.getZdarzenia().size();
        data = encode(zbiorZdarzen.getZdarzenia());
    }

    private static List<Zdarzenie> wrap(Zdarzenie z) {
        List<Zdarzenie> w = new ArrayList<Zdarzenie>();
        w.add(z);
        return w;
    }

    public CacheableZbiorZdarzen(CacheableZbiorZdarzen zbiorZdarzen, Zdarzenie z) {
        this(zbiorZdarzen, wrap(z));
    }

    public CacheableZbiorZdarzen(CacheableZbiorZdarzen zbiorZdarzen, List<Zdarzenie> zz) {
        this.przedszkoleKeyId = zbiorZdarzen.przedszkoleKeyId;
        List<Zdarzenie> noweZdarzenia = new ArrayList<Zdarzenie>();
        noweZdarzenia.addAll(zbiorZdarzen.decode());
        noweZdarzenia.addAll(zz);
        Collections.sort(noweZdarzenia);
        this.size = noweZdarzenia.size();
        this.data = encode(noweZdarzenia);
    }

    public CacheableZbiorZdarzen(final long przedszkoleKeyId, List<Zdarzenie> zz) {
        this.przedszkoleKeyId = przedszkoleKeyId;
        this.size = zz.size();
        data = encode(zz);
    }

    private byte[] encode(List<Zdarzenie> zz) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            for (Zdarzenie z : zz) {
                oos.writeLong(z.getKey().getId());
                //oos.writeLong(z.getPrzedszkoleKey().getId());
                oos.writeLong(z.getDzieckoKey().getId());
                oos.writeLong(z.getKartaKey().getId());
                oos.writeLong(z.getCzasZdarzenia().getTime());
                oos.writeByte(z.getTypZdarzenia().ordinal());
            }
            oos.flush();
            baos.close();
        } catch (IOException e) {
        }
        return baos.toByteArray();
    }

    public List<Zdarzenie> decode() {
        List<Zdarzenie> retval = new ArrayList<Zdarzenie>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Przedszkole przedszkole = IC.INSTANCE.findPrzedszkole(przedszkoleKeyId);
            for (int i = 0; i < size; i++) {
                Zdarzenie z = new Zdarzenie();
                z.setKey(KeyFactory.createKey("Zdarzenie", ois.readLong()));
                z.setPrzedszkoleKey(przedszkole.getKey());
                z.setDzieckoKey(KeyFactory.createKey(z.getPrzedszkoleKey(), "Dziecko", ois.readLong()));
                z.setKartaKey(KeyFactory.createKey(z.getDzieckoKey(), "Karta", ois.readLong()));
                z.setCzasZdarzenia(new Date(ois.readLong()));
                z.setTypZdarzenia(TypZdarzenia.values()[ois.readByte()]);
                retval.add(z);
            }
            ois.close();
        } catch (IOException ioe) {
        }

        return retval;
    }
}
