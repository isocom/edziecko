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

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class DumpPrzedszkole extends EDzieckoServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1914119596708215352L;

	@Override
	protected void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
		Przedszkole przedszkole = request.getPrzedszkole();
		response.println("<h2>Dzieci: " + przedszkole.getDzieci().size() + "</h2>");

		response.println("<table border=1>");
		response.println("<thead><tr>");
		response.println("<td>LP</td>");
		response.println("<td>Klucz</td>");
		response.println("<td>Imię i nazwisko</td>");
		response.println("<td>PESEL</td>");
		response.println("<td>Grupa</td>");
		response.println("<td>Aktywne</td>");
		response.println("</tr>");
		response.println("<tr>");
		response.println("<td>---</td>");
		response.println("<td>lp</td>");
		response.println("<td>Klucz</td>");
		response.println("<td>Numer karty</td>");
		response.println("<td>Numer seryjny</td>");
		response.println("<td>Aktywna</td>");
		response.println("</tr></thead>");

		int c1 = 0, c2;
		for (Dziecko d : przedszkole.getDzieci()) {
			response.println("<tr>");
			response.println("<td>" + (++c1) + ".</td>");
			response.println("<td><b>" + d.getKey().getId() + "</b></td>");
			response.println("<td>" + d.getImieNazwisko() + "</td>");
			response.println("<td>" + (d.getPesel() != null ? d.getPesel() : "") + "</td>");
			response.println("<td>" + (d.getGrupa() != null ? d.getGrupa().getCategory() : "") + "</td>");
			response.println("<td>" + (d.isAktywne() ? "Tak" : "Nie") + "</td>");
			response.println("</tr>");
			c2 = 0;
			for (Karta k : d.getKarty()) {
				response.println("<tr>");
				response.println("<td></td>");
				response.println("<td>" + (char) ('a' + c2++) + ".</td>");
				response.println("<td>" + k.getKey().getId() + "</td>");
				response.println("<td>*" + k.getNumerKarty() + "*</td>");
				response.println("<td>*" + k.getNumerSeryjny() + "*</td>");
				response.println("<td>" + (k.isAktywna() ? "Tak" : "Nie") + "</td>");
				response.println("</tr>");
			}
		}
		response.println("</table>");
	}

	@Override
	protected void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception {
	}

	@Override
	protected String getTitle() {
		return "Dump Przedszkola";
	}

	@Override
	protected PDFType getPDFType() {
		return PDFType.DumpPrzedszkole;
	}

    @Override
    protected XLSType getXLSType() {
        return XLSType.DumpPrzedszkole;
    }

}
