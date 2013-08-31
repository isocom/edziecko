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

public class HTMLHelper {
	
	public static String getStartDocument(String title) {
		String s = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
				"<title>EDziecko - "+title+"</title>" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/style.css\" />" +
				"</head>" +
				"<body>" +
				"<div id=\"bg\">" +
				"<div id=\"wrap\">" +
				"<div id=\"header\">" +
				"<ul id=\"nav\">" +
				"<li class=\"h\"><a href=\"\">Strona główna</a></li>" +
				"</ul>" +
				"</div><!-- /header -->" +
				"\n<div id=\"content\">";
		return s;
	}

	public static String getEndDocument() {
		String s = "\n</div>" +
				"<!-- /content -->" +
				"<div class=\"clearfix\"></div>" +
				"<div id=\"footer\">" +
				"<div id=\"ftinner\">" +
				"<div class=\"ftlink fl\">" +
				"<p id=\"copyright\">© 2011 by BPP. All Rights Reserved.<br/>" +
				"</div>" +
				"</div>" +
				"</div>" +
				"<!-- /footer -->" +
				"</div>" +
				"</div>" +
				"</body>" +
				"</html>";
		return s;
	}
}
