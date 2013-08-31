package name.prokop.bart.gae.edziecko.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Uzytkownik;
import name.prokop.bart.gae.edziecko.reports.pdf.PDFType;
import name.prokop.bart.gae.edziecko.reports.xls.XLSType;

public abstract class EDzieckoServlet extends HttpServlet {

    private static final long serialVersionUID = -5361561139800510871L;

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkCredentials(req, resp)) {
            return;
        }
        EDzieckoRequest request = new EDzieckoRequest(req);
        EDzieckoResponse response = new EDzieckoResponse(resp);
        try {
            onGet(request, response);
            response.producePage(this, request);
        } catch (Exception e) {
            response.handleException(e, request);
        }
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkCredentials(req, resp)) {
            return;
        }
        EDzieckoRequest request = new EDzieckoRequest(req);
        EDzieckoResponse response = new EDzieckoResponse(resp);
        try {
            onPost(new EDzieckoRequest(req), response);
            response.producePage(this, request);
        } catch (Exception e) {
            response.handleException(e, request);
        }
    }

    protected abstract void onGet(EDzieckoRequest request, EDzieckoResponse response) throws Exception;

    protected abstract void onPost(EDzieckoRequest request, EDzieckoResponse response) throws Exception;

    protected abstract String getTitle();

    protected abstract PDFType getPDFType();

    protected abstract XLSType getXLSType();

    protected String getScript() {
        return null;
    }

    public static boolean checkCredentials(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();
        Principal userPrincipal = request.getUserPrincipal();

        if (userPrincipal != null) {
            if (request.getSession().getAttribute(EDzieckoRequest.LOGOUT_URL) == null) {
                request.getSession().setAttribute(EDzieckoRequest.LOGOUT_URL, userService.createLogoutURL("http://edziecko.isocom.eu/"));
            }
            if (request.getParameter("przedszkoleId") != null) {
                Key przedszkoleKey = KeyFactory.createKey("Przedszkole", Long.valueOf(request.getParameter("przedszkoleId")));
                request.getSession().setAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID, przedszkoleKey);
            }
            if (request.getSession().getAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID) == null) {
                if (userService.isUserAdmin()) {
                    response.sendRedirect("UstawKluczPrzedszkola");
                    return false;
                } else {
                    Key przedszkoleKey = Uzytkownik.findPrzedszkoleKey(userPrincipal.getName());
                    if (przedszkoleKey == null) {
                        response.sendRedirect((String) request.getSession().getAttribute(EDzieckoRequest.LOGOUT_URL));
                        return false;
                    } else {
                        request.getSession().setAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID, przedszkoleKey);
                    }
                }
            }
            return true;
        } else {
            request.getSession().removeAttribute(EDzieckoRequest.LOGOUT_URL);
            request.getSession().removeAttribute(EDzieckoRequest.SESSION_PRZEDSZKOLE_ID);
            response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
            return false;
        }
    }
}
