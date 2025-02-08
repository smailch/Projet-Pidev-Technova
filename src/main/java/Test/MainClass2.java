package Test;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MainClass2 {
    public static void main(String[] args) {

        // provide recipient's email ID
        String to = "smail.chemlali@esprit.tn";
        // provide sender's email ID
        String from = "chemlaliismail388@gmail.com";

        // provide Mailtrap's username
        final String username = "chemlaliismail388@gmail.com";
        final String password = "copj rnsn hcix utzr";

        // provide Mailtrap's host address
        String host = "smtp.gmail.com";

        // configure Mailtrap's SMTP details
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // create the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // create a MimeMessage object
            Message message = new MimeMessage(session);
            // set From email field
            message.setFrom(new InternetAddress(from));
            // set To email field
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // set email subject field
            message.setSubject("Hello from the Mailtrap team");
            // set the content of the email message
            message.setText("Enjoy sending emails from Jakarta Mail!");

            // send the email message
            Transport.send(message);

            System.out.println("Email Message Sent Successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}