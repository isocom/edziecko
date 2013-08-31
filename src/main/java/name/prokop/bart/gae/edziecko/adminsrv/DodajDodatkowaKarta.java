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
package name.prokop.bart.gae.edziecko.adminsrv;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

import com.google.appengine.api.datastore.KeyFactory;

public class DodajDodatkowaKarta extends HttpServlet {

	private static final long serialVersionUID = -8366576781205779068L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream os = response.getOutputStream();
		try {
			InputStream is = getClass().getResourceAsStream("DodajDodatkowaKarta.html");
			while (is.available() > 0) {
				byte[] buffer = new byte[1024];
				int len = is.read(buffer);
				os.write(buffer, 0, len);
			}
		} finally {
			os.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String p = request.getParameter("p");
		String d = request.getParameter("d");
		String cn = request.getParameter("cn").trim();
		String sn = request.getParameter("sn").trim();

		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		PersistenceManager pm = PMF.getPM();
		try {
			pm.currentTransaction().begin();
			Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, KeyFactory.createKey("Przedszkole", Long.parseLong(p)));
			out.println(przedszkole);
			out.println("dzieci.size: " + przedszkole.getDzieci().size());
			Dziecko dziecko = pm.getObjectById(Dziecko.class, KeyFactory.createKey(przedszkole.getKey(), "Dziecko", Long.parseLong(d)));
			out.println("dziecko.karty.size: " + dziecko.getKarty().size());
			dziecko.add(new Karta(sn, cn));
			out.println("dziecko.karty.size: " + dziecko.getKarty().size());
			pm.currentTransaction().commit();
			IC.INSTANCE.replace(przedszkole);
			out.println("Zmieniono: " + dziecko);
			for (Karta k : dziecko.getKarty()) {
				out.println(" - " + k);
			}
		} catch (Exception ex) {
			ex.printStackTrace(out);
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
		out.close();
	}

}
