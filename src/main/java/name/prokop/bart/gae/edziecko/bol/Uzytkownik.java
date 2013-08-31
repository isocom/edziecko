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
import com.google.appengine.api.users.User;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import name.prokop.bart.gae.edziecko.util.PMF;

@PersistenceCapable
public class Uzytkownik implements LogicEntity {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    /**
     * Użytkownik, którego dotyczą aktualne dane logowania
     */
    @Persistent
    private User user;
    /**
     * Jeśli różne od null, pokazuje do którego przedszkola ma dostęp dany
     * login.
     */
    @Persistent
    private Key przedszkoleKey = null;

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Key getPrzedszkoleKey() {
        return przedszkoleKey;
    }

    public void setPrzedszkoleKey(Key przedszkoleKey) {
        this.przedszkoleKey = przedszkoleKey;
    }

    @Override
    public void persist(PersistenceManager pm) {
        if (key != null) {
            throw new IllegalStateException("key != null " + Uzytkownik.class);
        }
        if (user == null) {
            throw new IllegalArgumentException("user == null " + Uzytkownik.class);
        }

        Query q = pm.newQuery(Uzytkownik.class, "user == nowyUser");
        q.declareParameters("String nowyUser");
        @SuppressWarnings("unchecked")
        List<Uzytkownik> results = (List<Uzytkownik>) q.execute(this.user);
        if (results.isEmpty()) {
            pm.makePersistent(this);
        } else {
            throw new IllegalArgumentException("not unique " + Uzytkownik.class);
        }
    }

    public static Key findPrzedszkoleKey(String userName) {
        PersistenceManager pm = PMF.getPM();
        Query q = pm.newQuery(Uzytkownik.class);
        q.setFilter("(user==userParam)");
        q.declareParameters(User.class.getName() + " userParam");
        try {
            @SuppressWarnings("unchecked")
            List<Uzytkownik> l = (List<Uzytkownik>) q.execute(new User(userName, "gmail.com"));
            if (l.size() > 0) {
                return l.get(0).getPrzedszkoleKey();
            }
        } finally {
            q.closeAll();
            pm.close();
        }
        return null;
    }
}
