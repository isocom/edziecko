package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.IC;
import name.prokop.bart.gae.edziecko.util.PMF;

public class UpdateTest1 extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1914119596708215352L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		PersistenceManager pm = PMF.getPM();
		try {
			pm.currentTransaction().begin();
			Przedszkole swilcza = pm.getObjectById(Przedszkole.class, Long.parseLong(request.getParameter("przedszkoleId")));
			out.println(swilcza);
			swilcza.setAdres(null);
			pm.currentTransaction().commit();
			IC.INSTANCE.replace(swilcza);
		} catch (Exception ex) {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			ex.printStackTrace(out);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("null")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		Iterator<Karta> i = null;// IC.getInstance().getKarty().values().iterator();

		int counter = 25;
		while (i.hasNext()) {
			Karta k = i.next();
			out.println(k);
			PersistenceManager pm = PMF.getPM();
			try {
				pm.currentTransaction().begin();
				k = pm.getObjectById(Karta.class, k.getKey());
				k.setNumerKarty(k.getNumerKarty().trim());
				pm.currentTransaction().commit();
			} catch (Exception ex) {
				if (pm.currentTransaction().isActive())
					pm.currentTransaction().rollback();
				ex.printStackTrace(out);
			} finally {
				pm.close();
			}
			i.remove();
			out.println(k);
			if (counter-- == 0)
				break;
		}
		out.close();
	}

}
