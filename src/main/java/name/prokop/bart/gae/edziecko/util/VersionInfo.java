/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import name.prokop.bart.gae.edziecko.util.mailer.ExceptionReportMail;
import name.prokop.bart.gae.edziecko.util.mailer.Mailer;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class VersionInfo {

    private static final String VER_FORMAT = "yy.MMdd.HHmm";

    public static String retrieveVersionInfo(Class<?> classRef) {
        URL classResource = classRef.getResource(classRef.getSimpleName() + ".class");

        if (classResource.getProtocol().equalsIgnoreCase("file")) {
            try {
                URLConnection connection = classResource.openConnection();
                long lastModified = connection.getLastModified();
                Date lastModifiedDate = new Date(lastModified);
                connection.getInputStream().close();
                return DateToolbox.getFormatedDate(VER_FORMAT, lastModifiedDate);
            } catch (IOException e) {
            }
        }

        if (classResource.getProtocol().equalsIgnoreCase("jar")) {
            try {
                JarFile jarFile = new JarFile(new File(classRef.getProtectionDomain().getCodeSource().getLocation().toURI()));
                String entryPath = classRef.getName().replace('.', '/') + ".class";
                JarEntry jarEntry = jarFile.getJarEntry(entryPath);
                Date compiledTime = new Date(jarEntry.getTime());
                jarFile.close();
                return DateToolbox.getFormatedDate(VER_FORMAT, compiledTime);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return "00.0000.0000";
    }

    public static String getVersionInfo() {
        return retrieveVersionInfo(VersionInfo.class);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getVersionInfo());
    }
}
