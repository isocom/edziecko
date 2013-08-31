/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util.mailer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author prokob01
 */
public class Mail {

    private final List<MailAddress> to = new ArrayList<MailAddress>();
    private final List<MailAddress> cc = new ArrayList<MailAddress>();
    private final List<MailAddress> bcc = new ArrayList<MailAddress>();
    private String subject;
    private String textBody;

    public void addTo(MailAddress mailAddress) {
        to.add(mailAddress);
    }

    public void addCc(MailAddress mailAddress) {
        cc.add(mailAddress);
    }

    public void addBcc(MailAddress mailAddress) {
        bcc.add(mailAddress);
    }

    public List<MailAddress> getTo() {
        return to;
    }

    public List<MailAddress> getCc() {
        return cc;
    }

    public List<MailAddress> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }
}
