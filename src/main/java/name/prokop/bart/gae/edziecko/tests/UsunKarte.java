package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

public class UsunKarte extends HttpServlet {

    private static final long serialVersionUID = -2415533117587394710L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pwd = request.getParameter("passwd");
        if (!"TTSoft".equals(pwd)) {
            response.sendError(401);
            return;
        }

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        PersistenceManager pm = PMF.getPM();
        Long pId = Long.parseLong(request.getParameter("pId"));
        pm.currentTransaction().begin();
        Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, pId);
        out.println(przedszkole);
        long dId = Long.parseLong(request.getParameter("dId"));
        for (Dziecko d : przedszkole.getDzieci()) {
            if (d.getKey().getId() != dId) {
                continue;
            }
            long kId = Long.parseLong(request.getParameter("kId"));
            Iterator<Karta> iterator = d.getKarty().iterator();
            while (iterator.hasNext()) {
                Karta k = iterator.next();
                if (k.getKey().getId() == kId) {
                    iterator.remove();
                }
            }
        }
        pm.currentTransaction().commit();
        IC.INSTANCE.replace(przedszkole);

        pm.close();
        out.close();
    }
}
