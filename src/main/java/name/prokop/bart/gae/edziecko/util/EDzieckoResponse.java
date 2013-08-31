package name.prokop.bart.gae.edziecko.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.mailer.ExceptionReportMail;
import name.prokop.bart.gae.edziecko.util.mailer.Mailer;

public class EDzieckoResponse {
    
    private final HttpServletResponse response;
    private final StringBuilder out = new StringBuilder();
    
    public EDzieckoResponse(final HttpServletResponse response) {
        this.response = response;
    }
    
    public HttpServletResponse getResponse() {
        return response;
    }
    
    public StringBuilder getOut() {
        return out;
    }
    
    public void print(String... strings) {
        for (String s : strings) {
            out.append(s);
        }
    }
    
    public void println(String... strings) {
        for (String s : strings) {
            out.append(s).append('\n');
        }
    }
    
    void producePage(EDzieckoServlet servlet, EDzieckoRequest request) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            Przedszkole przedszkole = request.getPrzedszkole();
            
            writer.println("<html><head>");
            writer.println("<title>EDziecko: " + servlet.getTitle() + "</title>");
            writer.println(buildStyleElement());
            writer.println("</head><body>");
            String script = servlet.getScript();
            if (script != null) {
                writer.println("<script>");
                writer.println(script);
                writer.println("</script>");
            }
            writer.println("<div>");
            writer.println("<a href=\"Menu\">Start</a>");
            writer.println("<b>Jednostka:</b> " + przedszkole.getNazwa() + ". ");
            writer.println("<b>Adres:</b> " + przedszkole.getAdres().getAddress() + ". <b>NIP:</b> " + przedszkole.getNip() + ". ");
            writer.println("<a href=\"" + request.getLogoutUrl() + "\">Wyloguj " + request.getUserPrincipal().getName() + "</a>.");
            writer.println("</div>");
            writer.println("<hr>");
            writer.println(this.out.toString());
            writer.println("<hr>");
            writer.println("<a href=\"Menu\">Start</a>");
            if (servlet.getPDFType() != null) {
                writer.print("<a href=\"PDF?type=" + servlet.getPDFType().name() + "\">PDF</a>");
                writer.println(", ");
            }
            if (servlet.getXLSType() != null) {
                writer.print(servlet.getXLSType().buildLink());
                writer.println(", ");
            }
            writer.println("<b>GAE ID:</b> " + przedszkole.getKey().getId() + ". ");
            writer.println("Licencja do: " + DateToolbox.getFormatedDate("yyyy-MM-dd", przedszkole.getTerminLicencji()) + ". ");
            writer.println("Wersja: " + VersionInfo.getVersionInfo() + ". ");
            writer.println("Generowano: " + (System.currentTimeMillis() - request.getTime()) + " ms. ");
            writer.println("<a href=\"" + request.getLogoutUrl() + "\">Wyloguj " + request.getUserPrincipal().getName() + "</a>.");
            writer.println("</div>");
            writer.println("</body></html>");
        } finally {
            writer.close();
        }
    }
    
    @SuppressWarnings("unused")
    private String getParams(EDzieckoRequest request) {
        String retVal = "";
        Enumeration parameterNames = request.getRequest().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement().toString();
            retVal += "&" + name + "=" + request.getParameter(name);
        }
        return retVal;
    }
    
    public void handleException(Exception e, EDzieckoRequest request) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            writer.println("<html><body><pre>");
            writer.println(e.getMessage());
            writer.println("**********************************************");
            e.printStackTrace(writer);
            writer.println("</pre></body></html>");
        } finally {
            writer.close();
        }
        
        StringBuilder info = new StringBuilder();
        
        try {
            Przedszkole przedszkole = request.getPrzedszkole();
            info.append("Przedszkole\n").append(przedszkole).append("----------------------------------\n");
        } catch (Exception x) {
        }
        
        Enumeration attributeNames = request.getRequest().getSession().getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String nextElement = (String) attributeNames.nextElement();
            info.append("Session - ").append(nextElement).append("=").append(request.getRequest().getSession().getAttribute(nextElement)).append('\n');
        }
        Enumeration parameterNames = request.getRequest().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String nextElement = (String) parameterNames.nextElement();
            info.append("Parameter - ").append(nextElement).append('=').append(request.getRequest().getParameter(nextElement)).append('\n');
        }
        
        Mailer.INSTANCE.send(new ExceptionReportMail(e, info.toString()));
    }
    
    public void sendRedirect(String string) throws IOException {
        response.sendRedirect(string);
    }
    
    private static String buildStyleElement() {
        StringBuilder sb = new StringBuilder();
        sb.append("<style type=\"text/css\">").append('\n');
        sb.append("body { background-color:#e0e0e0; }").append('\n');
        sb.append("h1 { color:blue; text-align:left; }").append('\n');
        sb.append("table { border-collapse:collapse; }").append('\n');
        sb.append("table,th, td { border: 1px solid black; }").append('\n');
        // sb.append("p { font-family:\"Times New Roman\"; font-size:20px; }").append('\n');
        // unvisited link
        sb.append("a:link {background-color:#FFFF85;}").append('\n');
        // visited link
        sb.append("a:visited {background-color:#FFFF85;}").append('\n');
        // mouse over link
        sb.append("a:hover {background-color:#FF704D;}").append('\n');
        // selected link
        sb.append("a:active {background-color:#FF0000;}").append('\n');
        sb.append("</style>");
        return sb.toString();
    }
}
