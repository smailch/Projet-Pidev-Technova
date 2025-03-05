
package services;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;


public class Mail_rec {

   public static void sendMail(String receveursList,String object,String corps) {
      Properties properties = new Properties();

      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");

      String MonEmail = "chemlaliismail388@gmail.com";
      String password = "copj rnsn hcix utzr";


      // Create session
      Session session = Session.getInstance(properties, new Authenticator() {
         @Override
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(MonEmail, password);
         }
      });

      try {
         // Create message
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(MonEmail));
         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receveursList));
         message.setSubject(object);
         message.setText(corps);

         // Send email
         Transport.send(message);
         System.out.println("Email sent successfully to " + receveursList);

      } catch (MessagingException e) {
         e.printStackTrace();
      }
   }
   /**
    * Sends an email with the generated PDF attached.
    * @param recipient The recipient's email address.
    * @param pdfFilePath The path of the PDF file to send.
    */
   public static void sendEmailWithPdf(String recipient, String pdfFilePath) {
      final String senderEmail = "chemlaliismail388@gmail.com";
      final String senderPassword = "copj rnsn hcix utzr";

      Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");

      Session session = Session.getInstance(properties, new Authenticator() {
         @Override
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
         }
      });

      try {
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(senderEmail));
         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
         message.setSubject("Votre document PDF");

         BodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setText("Bonjour,\n\nVeuillez trouver ci-joint votre document généré.\n\nCordialement,");

         MimeBodyPart attachmentPart = new MimeBodyPart();
         attachmentPart.attachFile(new File(pdfFilePath));

         Multipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageBodyPart);
         multipart.addBodyPart(attachmentPart);

         message.setContent(multipart);

         Transport.send(message);
         System.out.println("Email sent successfully!");

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
