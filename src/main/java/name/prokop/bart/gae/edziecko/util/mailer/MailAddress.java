/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util.mailer;

/**
 *
 * @author Bartłomiej P. Prokop
 */
public class MailAddress {

    private final String fullName;
    private final String emailAddress;

    public MailAddress(String fullName, String emailAddress) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}