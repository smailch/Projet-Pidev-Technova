//package services;
//
//import entities.DocumentAdministratif;
//import interfaces.IService;
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeBodyPart;
//import jakarta.mail.internet.MimeMessage;
//import jakarta.mail.internet.MimeMultipart;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Base64;
//import java.util.Properties;
//
//public class SendMail  {
//    public void envoyerMail(){
//        String to = "ichaabane6@gmail.com"; // Destinataire
//        String from = ; // Votre e-mail
//        final String username = "ichaabane6@gmail.com"; // Votre e-mail
//        final String password = "jdcw rpfu qoex yddo"; // Mot de passe d'application Gmail
//
//        String host = "smtp.gmail.com"; // Serveur SMTP Gmail
//
//        // 🖼 Chemin du logo (⚠️ Vérifiez que l'image existe)
//        String imagePath = "C:\\Users\\ichaa\\Downloads\\dossier\\Projet-Pidev-Technova-Impot\\src\\main\\resources\\assets\\images\\logo.png";
//
//        // Configuration SMTP
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", host);
//        props.put("mail.smtp.port", "587");
//
//        // Création de la session SMTP
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//        try {
//            // Lecture du logo en Base64 pour intégration dans l'email
//            String base64Image = "";
//            File imageFile = new File(imagePath);
//            if (imageFile.exists()) {
//                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
//                base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
//            } else {
//                System.out.println("❌ Erreur : L'image du logo n'existe pas à l'emplacement : " + imagePath);
//                return;
//            }
//
//            // 🌟 Contenu HTML de l'email
//            String emailContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
//                    + "<div style='text-align: center;'>"
//                    + "<img src='" + base64Image + "' alt='CiviSmart Logo' style='width: 150px; margin-bottom: 20px;'>"
//                    + "</div>"
//                    + "<h2 style='color: #2d89ef; text-align: center;'>Bienvenue sur <b>CiviSmart</b> 🎉</h2>"
//                    + "<p style='font-size: 16px; color: #333;'>Bonjour <b>" + utilisateur.getPrenom() + "</b>,</p>"
//                    + "<p style='font-size: 14px; color: #555;'>"
//                    + "Votre compte a été créé avec succès sur <b>CiviSmart</b> ! 🚀 Nous sommes ravis de vous accueillir."
//                    + "</p>"
//                    + "<p style='font-size: 14px; color: #555;'>"
//                    + "Connectez-vous dès maintenant et découvrez toutes nos fonctionnalités."
//                    + "</p>"
//                    + "<div style='text-align: center; margin: 20px;'>"
//                    + "<a href='https://www.civismart.com' style='background-color: #2d89ef; color: #fff; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 16px;'>"
//                    + "Accéder à CiviSmart</a>"
//                    + "</div>"
//                    + "<p style='font-size: 12px; color: #888; text-align: center;'>"
//                    + "Si vous avez des questions, n'hésitez pas à nous contacter."
//                    + "<br>&copy; 2025 CiviSmart - Tous droits réservés.</p>"
//                    + "</div>";
//
//            // Création du message principal
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            message.setSubject("🎉 Bienvenue sur CiviSmart !");
//
//            // Ajout du contenu HTML
//            MimeBodyPart htmlPart = new MimeBodyPart();
//            htmlPart.setContent(emailContent, "text/html; charset=utf-8");
//
//            // Création du multipart contenant le HTML
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(htmlPart);
//
//            // Ajout du contenu au message
//            message.setContent(multipart);
//
//            // Envoi du message
//            Transport.send(message);
//            System.out.println("📧 Email professionnel envoyé avec succès à " + utilisateur.getEmail());
//
//        } catch (Exception e) {
//            System.out.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
//        }
//    }
//}
//}
