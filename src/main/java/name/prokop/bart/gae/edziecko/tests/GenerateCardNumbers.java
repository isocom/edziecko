package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.prokop.bart.gae.edziecko.util.CardNumberToolbox;
import name.prokop.bart.gae.edziecko.util.StringToolbox;

public class GenerateCardNumbers extends HttpServlet {

    private static final long serialVersionUID = 862522900784952793L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        int cardPrefix = new Random().nextInt() % 10000;
        if (request.getParameter("prefix") != null) {
            cardPrefix = Integer.valueOf(request.getParameter("prefix"));
        }
        if (cardPrefix < 0) {
            cardPrefix *= -1;
        }

        out.println("Prefix: " + cardPrefix);
        out.println();
        for (int i = 1; i <= 500; i++) {
            out.println(StringToolbox.cardNumberPretty(CardNumberToolbox.generateType0(cardPrefix, i)));
        }

        out.close();
    }
}
