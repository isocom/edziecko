/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class PingServlet extends HttpServlet {

    private static final String PING = "ping";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        try {
            String token = req.getParameter("token");
            writer.println("token: " + token);
            String command = req.getParameter("command");
            writer.println("command: " + command);
            String remoteAddr = req.getRemoteAddr();
            writer.println("remote addres: " + remoteAddr);

            if (token == null || token.trim().equals("")) {
                writer.println("No valid token");
                return;
            }

            PingServletData data = (PingServletData) IC.INSTANCE.getCachedObject(buildUUID(token));
            if (data == null && command != null && command.trim().equalsIgnoreCase(PING)) {
                data = new PingServletData();
                data.setIpAddress(remoteAddr);
                IC.INSTANCE.addCachedObject(buildUUID(token), data);
                writer.println("No data in cache, saving and exiting");
                return;
            }
            if (data == null) {
                writer.println("No data in cache, and no command set");
                return;
            }

            writer.println("Current data for token is: " + data);

            if (!data.getIpAddress().equals(remoteAddr) && command != null && command.trim().equalsIgnoreCase(PING)) {
                data.setDate(new Date());
                data.setIpAddress(remoteAddr);
                IC.INSTANCE.addCachedObject(buildUUID(token), data);                
                writer.println("data updated:");
                updateHE(writer, remoteAddr);
            }
        } catch (Exception e) {
            e.printStackTrace(writer);
        } finally {
            writer.close();
        }
    }

    private String buildUUID(String token) {
        return "PingServlet-" + token;
    }

    public void updateHE(PrintWriter writer, String ip) throws Exception {
        writer.println("Updating HE:");
        String urlString = "https://ipv4.tunnelbroker.net/nic/update?username=";
        urlString += URLEncoder.encode("bartprokop", "UTF-8");
        urlString += "&password=" + URLEncoder.encode("wTpr5mo!W", "UTF-8");
        urlString += "&hostname=" + URLEncoder.encode("170306", "UTF-8");
        urlString += "&myip=" + URLEncoder.encode(ip, "UTF-8");
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            writer.println(line);
        }
        reader.close();
    }
}
