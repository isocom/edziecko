package name.prokop.bart.gae.edziecko.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.security.Principal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.bol.RozliczenieMiesieczne;
import name.prokop.bart.gae.edziecko.reports.KidsReport;
import name.prokop.bart.gae.edziecko.reports.ZbiorZdarzen;

public class EDzieckoRequest {

    public static final String SESSION_PRZEDSZKOLE_ID = "PRZEDSZKOLE_ID";
    static final String LOGOUT_URL = "LOGOUT_URL";
    private static final String SESSION_ROK_MIESIAC_ID = "ROK_MIESIAC";
    private final HttpServletRequest request;
    private transient Przedszkole przedszkole = null;
    private transient RozliczenieMiesieczne rozliczenieMiesieczne = null;
    private transient KidsReport kidsReport = null;
    private final long time = System.currentTimeMillis();

    public EDzieckoRequest(HttpServletRequest request) {
        this.request = request;

        if (request.getParameter("rokMiesiac") != null) {
            request.getSession().setAttribute(SESSION_ROK_MIESIAC_ID, request.getParameter("rokMiesiac"));
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getParameter(String string) {
        return request.getParameter(string);
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public HttpSession getSession() {
        return request.getSession();
    }

    long getTime() {
        return time;
    }

    public boolean isAdmin() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.isUserAdmin();
    }

    public String getLogoutUrl() {
        return (String) request.getSession().getAttribute(LOGOUT_URL);
    }

    public Key getPrzedszkoleKey() {
        return (Key) request.getSession().getAttribute(SESSION_PRZEDSZKOLE_ID);
    }

    public int getRokMiesiac() {
        return Integer.parseInt((String) request.getSession().getAttribute(SESSION_ROK_MIESIAC_ID));
    }

    public Date decodeRokMiesiacFrom() {
        return decodeRokMiesiacFrom(getRokMiesiac());
    }

    public Date decodeRokMiesiacTo() {
        return decodeRokMiesiacTo(getRokMiesiac());
    }

    public static Date decodeRokMiesiacFrom(int rokMiesiac) {
        return DateToolbox.getBeginningOfMonth(rokMiesiac / 100, rokMiesiac % 100 - 1);
    }

    public static Date decodeRokMiesiacTo(int rokMiesiac) {
        return DateToolbox.getEndOfMonth(rokMiesiac / 100, rokMiesiac % 100 - 1);
    }

    public synchronized Przedszkole getPrzedszkole() {
        if (przedszkole == null) {
            przedszkole = IC.INSTANCE.findPrzedszkole(getPrzedszkoleKey());
        }
        return przedszkole;
    }

    public synchronized RozliczenieMiesieczne retrieveRozliczenieMiesieczne() {
        if (rozliczenieMiesieczne == null) {
            rozliczenieMiesieczne = IC.INSTANCE.retrieveRozliczenieMiesieczne(getPrzedszkoleKey(), getRokMiesiac());
        }
        return rozliczenieMiesieczne;
    }

    public synchronized RozliczenieMiesieczne retrievePreviousRozliczenieMiesieczne() {
        int rokMiesiac = getRokMiesiac();
        int r = rokMiesiac / 100;
        int m = rokMiesiac % 100;
        m -= 1;
        if (m == 0) {
            m = 12;
            r -= 1;
        }
        rokMiesiac = r * 100 + m;
        return IC.INSTANCE.retrieveRozliczenieMiesieczne(getPrzedszkoleKey(), rokMiesiac);
    }

    public KidsReport getKidsReport() {
        if (kidsReport == null) {
            kidsReport = new KidsReport(ZbiorZdarzen.getZbiorZdarzen(this), retrieveRozliczenieMiesieczne());
        }
        return kidsReport;
    }
}
