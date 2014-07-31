/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class PingServletData implements Serializable {

    private Date date = new Date();
    private String ipAddress;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return date + ", " + ipAddress;
    }
}
