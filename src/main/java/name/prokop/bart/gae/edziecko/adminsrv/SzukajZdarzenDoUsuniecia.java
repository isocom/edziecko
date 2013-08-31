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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SzukajZdarzenDoUsuniecia extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1914119596708215352L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.setContentType("text/plain;charset=UTF-8");
//		PrintWriter out = response.getWriter();
//
//		try {
//			Long id = Long.parseLong(request.getParameter("przedszkoleId"));
//			Przedszkole przedszkole = IC.getInstance().findPrzedszkole(id);
//			out.println(przedszkole);
//
//			ZbiorZdarzen zbiorZdarzen = new ZbiorZdarzen(przedszkole.getKey(), DateToolbox.encodeDate("20000101"), DateToolbox.encodeDate("20311025"));
//
//			Date now = new Date();
//			Date firstDeploy = DateToolbox.encodeDate("20110901");
//
//			for (Zdarzenie z : zbiorZdarzen.getZdarzenia()) {
//				if (true) {
//					//IC.getInstance().getZdarzeniaDoUsuniecia().add(z);
//					continue;
//				}
//				
//				if (z.getCzasZdarzenia().before(firstDeploy)) {
//					//IC.getInstance().getZdarzeniaDoUsuniecia().add(z);
//					out.println("Przed 2011-09-01: " + z);
//					continue;
//				}
//				if (z.getCzasZdarzenia().after(now)) {
//					out.println("Po " + now + ": " + z);
//					//IC.getInstance().getZdarzeniaDoUsuniecia().add(z);
//					continue;
//				}
//			}
//			out.println(zbiorZdarzen);
//		} catch (Exception e) {
//			e.printStackTrace(out);
//		} finally {
//			out.close();
//		}
    }
}
