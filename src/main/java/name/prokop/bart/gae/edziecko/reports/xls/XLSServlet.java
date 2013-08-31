package name.prokop.bart.gae.edziecko.reports.xls;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.util.EDzieckoRequest;
import name.prokop.bart.gae.edziecko.util.EDzieckoServlet;

public class XLSServlet extends HttpServlet {

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!EDzieckoServlet.checkCredentials(req, resp)) {
            return;
        }
        EDzieckoRequest request = new EDzieckoRequest(req);
        byte[] xls;
        XLSType type;
        try {
            type = XLSType.valueOf(request.getParameter("type"));
            XLSReport xlsReport = type.getRenderer();
            xls = xlsReport.produceXLS(request);
        } catch (Exception e) {
            e.printStackTrace(resp.getWriter());
            resp.getWriter().close();
            return;
        }
        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment; fileName=" + type.buildName(request));
        resp.getOutputStream().write(xls);
        resp.getOutputStream().close();
    }
}
