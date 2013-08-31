/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.tests;

import java.io.IOException;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class DownloadGarbage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte[] garbage = new byte[1024];
        ServletOutputStream outputStream = resp.getOutputStream();
        new Random().nextBytes(garbage);
        for (int i = 0; i < 1024 * 25; i++) {
            outputStream.write(garbage);
        }
        outputStream.close();
    }
}
