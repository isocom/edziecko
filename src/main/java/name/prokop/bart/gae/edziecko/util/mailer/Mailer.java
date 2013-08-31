/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.prokop.bart.gae.edziecko.util.mailer;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author prokob01
 */
public enum Mailer {

    INSTANCE;
    private final String fromAddr = "prokop.bart@gmail.com";
    private final String fromName = "EDziecko Error";

    public void send(Mail mail) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddr, fromName));
            for (MailAddress mailAddress : mail.getTo()) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress.getEmailAddress(), mailAddress.getFullName()));
            }
            msg.setSubject(mail.getSubject());
            msg.setText(mail.getTextBody());
            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Bład w mailerze", e);
        }
    }
}
