package com.example.foodMateFrontend;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "dwtintchev@gmail.com";
    private static final String SMTP_PASSWORD = "bpsx vpaa inst ifkt";

    private static final String EMAIL_FROM = "dwtintchev@gmail.com";
    private static final String EMAIL_TO = "wang.zitin@northeastern.edu";
    private static final String EMAIL_SUBJECT = "Test Email";
    private static final String EMAIL_TEXT = "This is a test email.";

    public static void sendEmail(String to, String code) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", SMTP_SERVER);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(EMAIL_SUBJECT);
            message.setText(code);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (MessagingException ignored) {

                    }
                }
            }).start();

        } catch (MessagingException ignored) {

        }
    }

}
