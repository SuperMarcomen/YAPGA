package it.marcodemartino.yapga.common.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailProvider implements EmailProvider {

    private final Logger logger = LogManager.getLogger(GmailProvider.class);
    private final String fromAddress;
    private final Session session;

    public GmailProvider(String fromAddress, String password) {
        this.fromAddress = fromAddress;
        Properties properties = initProperties();

        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, password);
            }
        });
    }

    private Properties initProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        return properties;
    }

    @Override
    public void sendEmail(String toAddress, String subject, String content) {
        try {
            trySendMail(toAddress, subject, content);
        } catch (MessagingException e) {
            logger.error("There was an error sending an email to {}. Error message: {}", toAddress, e.getMessage());
        }
    }

    private void trySendMail(String toAddress, String subject, String content) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=utf-8");

        logger.info("Sending an email to {}", toAddress);
        Transport.send(message);
        logger.info("The email to {} was sent successfully", toAddress);
    }
}
