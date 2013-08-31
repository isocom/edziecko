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

import com.pdfjet.*;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestPDF extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; fileName=TestPDF.pdf");
            OutputStream fos = resp.getOutputStream();
            
            PDF pdf = new PDF(fos);
            Font fontTimesRoman14 = new Font(pdf, CoreFont.TIMES_ROMAN);
            fontTimesRoman14.setSize(14);
            Font fontHelvetica14 = new Font(pdf, CoreFont.HELVETICA);
            fontHelvetica14.setSize(14);
            
            Page page = new Page(pdf, A4.PORTRAIT);
            double x_pos;
            double y_pos;
            x_pos = 20.0;
            y_pos = 20.0;
            TextLine text = new TextLine(fontTimesRoman14);
            
            text.setText("Raport jeszcze niedostępny - w opracowaniu");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("Żałować należałoby tego, że Śródborów nie posiadał");
            text.setPosition(x_pos, y_pos += 48);
            text.drawOn(page);
            
            text.setText("skrzyń na dżdżownice. Ówcześni właściciele zupełnie");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("się tym nie przejmowali. Źrebaki deptały po biednych");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("dżdżownicach, pełzających wśród ździebełek słomy.");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("Ćmy straszyły je nocą. Łatwe życie dżdżownic");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("to przebrzmiały mit!");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text = new TextLine(fontHelvetica14);
            
            text.setText("Żałować należałoby tego, że Śródborów nie posiadał");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("skrzyń na dżdżownice. Ówcześni właściciele zupełnie");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("się tym nie przejmowali. Źrebaki deptały po biednych");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("dżdżownicach, pełzających wśród ździebełek słomy.");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("Ćmy straszyły je nocą. Łatwe życie dżdżownic");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            text.setText("to przebrzmiały mit!");
            text.setPosition(x_pos, y_pos += 24);
            text.drawOn(page);
            
            pdf.flush();
            fos.close();
        } catch (Exception e) {
            resp.sendError(404, e.getMessage());
        }
    }
}
