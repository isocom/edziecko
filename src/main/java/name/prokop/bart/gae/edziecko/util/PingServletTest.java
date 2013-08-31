/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util;

import java.io.PrintWriter;

/**
 *
 * @author Administrator
 */
public class PingServletTest {
    public static void main(String[] args) throws Exception {
        new PingServlet().updateHE(new PrintWriter(System.out), "1.1.1.1");
    }
    
}
