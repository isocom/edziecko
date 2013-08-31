package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.bol.Dziecko;
import name.prokop.bart.gae.edziecko.bol.Karta;
import name.prokop.bart.gae.edziecko.bol.Przedszkole;
import name.prokop.bart.gae.edziecko.util.PMF;

public class UsunDodatkoweKarty extends HttpServlet {

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
		ArrayList<Karta> dousuniecia = new ArrayList<Karta>();
		Long id = Long.parseLong(request.getParameter("przedszkoleId"));
		pm.currentTransaction().begin();
		Przedszkole przedszkole = pm.getObjectById(Przedszkole.class, id);
		for (Dziecko d : przedszkole.getDzieci()) {
			List<Karta> karty = d.getKarty();
			while (karty.size() > 1) {
				Karta k = karty.get(1);
				out.println("Dodatkowa karta: " + k);
				dousuniecia.add(k);
				karty.remove(1);
			}
		}
		pm.currentTransaction().commit();

		for (Karta k : dousuniecia) {
			pm.deletePersistent(k);
			out.println("Usunieto: " + k);
		}
		pm.close();
		out.close();
	}

}
