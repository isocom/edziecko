package name.prokop.bart.gae.edziecko.adminsrv;

import com.google.appengine.api.datastore.Key;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.StringToolbox;
import org.json.JSONObject;

public class ExportujKD extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Przedszkole przedszkole;
            if (request.getParameter("przedszkoleId") != null) {
                Long id = Long.parseLong(request.getParameter("przedszkoleId"));
                przedszkole = IC.INSTANCE.findPrzedszkole(id);
            } else {
                Key key = (Key) request.getSession().getAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID);
                przedszkole = IC.INSTANCE.findPrzedszkole(key);
            }

            String s = przedszkole(przedszkole);
            out.println(s);

            JSONObject j = new JSONObject(s);
            out.println(j.get("cards"));
            out.println(j.get("humans"));
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.flush();
        }
    }

    private String przedszkole(Przedszkole przedszkole) {
        StringBuilder sb = new StringBuilder();
        Set<Karta> karty = new HashSet<Karta>();
        Set<Dziecko> dzieci = new HashSet<Dziecko>();

        for (Dziecko d : przedszkole.getDzieci()) {
            dzieci.add(d);
            for (Karta k : d.getKarty()) {
                karty.add(k);
            }
        }

        sb.append("{");
        sb.append("\"cards\":");
        sb.append("[");
        for (Karta k : karty) {
            sb.append(karta(k)).append(",\n");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("],");
        sb.append("\"humans\":");
        sb.append("[");
        for (Dziecko d : dzieci) {
            sb.append(dziecko(d)).append(",\n");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append("]");
        sb.append("}");

        return sb.toString();
    }

    private String karta(Karta karta) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"sn\":\"").append(karta.getNumerSeryjny().substring(2)).append("\"");
        sb.append(",");
        String k = karta.getNumerKarty();
        k = StringToolbox.cardNumberCompress(k);
        k = StringToolbox.cardNumberPretty(k);
        sb.append("\"cn\":\"").append(k).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String dziecko(Dziecko dziecko) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"n\":\"").append(dziecko.getImieNazwiskoAsString()).append("\"");
        sb.append(",");
        sb.append("\"cards\":").append(karty(dziecko));
        sb.append("}");
        return sb.toString();
    }

    private String karty(Dziecko dziecko) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Karta k : dziecko.getKarty()) {
            sb.append("\"");
            sb.append(k.getNumerSeryjny().substring(2));
            sb.append("\",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }
}
