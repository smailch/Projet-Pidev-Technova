package services;

import entities.Role;
import entities.Utilisateur;
import interfaces.IService;
import javafx.fxml.FXML;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import jakarta.mail.internet.*;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UtilisateurService implements IService<Utilisateur> {
    private JwtService jwtService = new JwtService();


    public void addEntity(Utilisateur utilisateur) {
        UtilisateurService utilisateurService = new UtilisateurService();

        // Vérifier si l'email existe déjà ou n'est pas valide
        if (utilisateurService.emailExists(utilisateur.getEmail()) || !(isValidEmail(utilisateur.getEmail()))) {
            System.out.println("L'email existe déjà ou l'email n'est pas valide !");
        } else {
            try {
                // Requête SQL mise à jour pour exclure le champ id
                String req = "INSERT INTO `utilisateur`(`Nom`, `Prenom`, `Email`, `Role`, `DateInscription`, `motDePasse`) VALUES (?, ?, ?, ?, ?, ?)";

                // Préparer la requête
                PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
                pst.setString(1, utilisateur.getNom());
                pst.setString(2, utilisateur.getPrenom());
                pst.setString(3, utilisateur.getEmail());
                pst.setString(4, utilisateur.getRole().name());
                pst.setDate(5, new java.sql.Date(utilisateur.getDateInscription().getTime()));

                // Hasher le mot de passe avant de l'enregistrer
                String motDePasseHashe = hasherMotDePasse(utilisateur.getMotDePasse());
                pst.setString(6, motDePasseHashe);

                // Exécuter la requête
                pst.executeUpdate();
                System.out.println("Utilisateur ajouté avec succès !");
                utilisateurService.EnvoyerEmail(utilisateur);  // 🔥 Appel de la méthode d'envoi d'email

            } catch (SQLException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }


    @Override
    public void deleteEntity(Utilisateur utilisateur) {
        try {
            String req = "DELETE FROM `utilisateur` WHERE id = " + utilisateur.getId();
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(req);
            System.out.println("Utilisateur supprimé");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Utilisateur utilisateur) {
        UtilisateurService utilisateurService = new UtilisateurService();
        if (!(utilisateurService.isValidEmail(utilisateur.getEmail()))) {
            System.out.println("L'email ne est pas valid  !");
        } else {


            try {
                String req = "UPDATE `utilisateur` SET `Nom`='" + utilisateur.getNom() +
                        "', `Prenom`='" + utilisateur.getPrenom() +
                        "', `Email`='" + utilisateur.getEmail() +
                        "', `Role`='" + utilisateur.getRole().name() +
                        "', `DateInscription`='" + utilisateur.getDateInscription() +
                        "' WHERE id = " + utilisateur.getId();
                Statement st = MyConnection.getInstance().getCnx().createStatement();
                st.executeUpdate(req);
                System.out.println("Utilisateur mis à jour");
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    @Override
    public List<Utilisateur> getAllData() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            String req = "SELECT * FROM `utilisateur`";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet res = st.executeQuery(req);
            while (res.next()) { //ResultSet /colonnes/table
                Utilisateur u = new Utilisateur();
                u.setId(res.getInt("id"));
                u.setNom(res.getString("Nom"));
                u.setPrenom(res.getString("Prenom"));
                u.setEmail(res.getString("Email"));
                u.setRole(Role.valueOf(res.getString("Role")));
                u.setDateInscription(res.getDate("DateInscription"));
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return utilisateurs;
    }
//email validation
    public static boolean isValidEmail(String email) {
        // Expression régulière pour valider un format d'email classique
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean emailExists(String email) {
        try {
            String req = "SELECT COUNT(*) FROM utilisateur WHERE Email = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, email);
            ResultSet res = pst.executeQuery();
            if (res.next()) {//ResultSet /colonnes/table
                return res.getInt(1) > 0; //ResultSet /ligne/table
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }



    public static void EnvoyerEmail(Utilisateur utilisateur) {
        String to = utilisateur.getEmail(); // Destinataire
        String from = "chemlaliismail388@gmail.com"; // Votre e-mail
        final String username = "chemlaliismail388@gmail.com"; // Votre e-mail
        final String password = "copj rnsn hcix utzr"; // Mot de passe d'application Gmail

        String host = "smtp.gmail.com"; // Serveur SMTP Gmail

        // 🖼 Chemin du logo (⚠️ Vérifiez que l'image existe)
        String imagePath = "C:\\Users\\ichaa\\Downloads\\dossier\\Projet-Pidev-Technova-Impot\\src\\main\\resources\\assets\\images\\logo.png";

        // Configuration SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Création de la session SMTP
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            // Lecture du logo en Base64 pour intégration dans l'email
            String base64Image = "";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
                base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            } else {
                System.out.println("❌ Erreur : L'image du logo n'existe pas à l'emplacement : " + imagePath);
                return;
            }

            // 🌟 Contenu HTML de l'email
            String emailContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
                    + "<div style='text-align: center;'>"
                    + "<img src='" + base64Image + "' alt='CiviSmart Logo' style='width: 150px; margin-bottom: 20px;'>"
                    + "</div>"
                    + "<h2 style='color: #2d89ef; text-align: center;'>Bienvenue sur <b>CiviSmart</b> 🎉</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Bonjour <b>" + utilisateur.getPrenom() + "</b>,</p>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "Votre compte a été créé avec succès sur <b>CiviSmart</b> ! 🚀 Nous sommes ravis de vous accueillir."
                    + "</p>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "Connectez-vous dès maintenant et découvrez toutes nos fonctionnalités."
                    + "</p>"
                    + "<div style='text-align: center; margin: 20px;'>"
                    + "<a href='https://www.civismart.com' style='background-color: #2d89ef; color: #fff; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 16px;'>"
                    + "Accéder à CiviSmart</a>"
                    + "</div>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>"
                    + "Si vous avez des questions, n'hésitez pas à nous contacter."
                    + "<br>&copy; 2025 CiviSmart - Tous droits réservés.</p>"
                    + "</div>";

            // Création du message principal
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("🎉 Bienvenue sur CiviSmart !");

            // Ajout du contenu HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContent, "text/html; charset=utf-8");

            // Création du multipart contenant le HTML
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);

            // Ajout du contenu au message
            message.setContent(multipart);

            // Envoi du message
            Transport.send(message);
            System.out.println("📧 Email professionnel envoyé avec succès à " + utilisateur.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }



    //passworrd  BCrypt


    public static String hasherMotDePasse(String motDePasseClair) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(motDePasseClair); // Hash le mot de passe et le retourne
    }

    public static boolean verifierMotDePasse(String motDePasseClair, String motDePasseHash) {
        // Initialisation de l'encodeur BCrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Vérification du mot de passe en clair par rapport au mot de passe haché
        return encoder.matches(motDePasseClair, motDePasseHash);
    }




    //connexion
    public Utilisateur connexion(String email, String motDePasse) {
        // Vérification de l'email et du mot de passe
        if (email == null || email.isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            System.out.println("⚠️ Email ou mot de passe vide !");
            return null;
        }

        // Vérification de la validité de l'email
        if (!isValidEmail(email)) {
            System.out.println("❌ Email invalide !");
            return null;
        }

        Utilisateur utilisateur = null;

        try {
            // Requête SQL avec tri croissant par id, nom, et email
            String req = "SELECT id, nom, prenom, email, role, dateInscription, motDePasse FROM utilisateur WHERE email = ? ORDER BY id ASC, nom ASC, email ASC";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            // Vérification si un utilisateur avec l'email existe
            if (rs.next()) {
                String motDePasseHash = rs.getString("motDePasse");
                // Vérification du mot de passe
                if (verifierMotDePasse(motDePasse, motDePasseHash)) {
                    // Utilisation du bon constructeur avec tous les paramètres
                    utilisateur = new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            Role.valueOf(rs.getString("role")),  // Si tu utilises une énumération Role
                            rs.getDate("dateInscription"),
                            motDePasseHash  // Ajout du mot de passe hashé
                    );
                    SessionManager.setUserId(utilisateur.getId());
                    // Affichage des informations de l'utilisateur
                    System.out.println("✅ Connexion réussie : " + utilisateur.getPrenom());
                    System.out.println("Utilisateur connecté : " + utilisateur.getNom() + " " + utilisateur.getPrenom());

                    // Génération du JWT

                    String jwtToken = jwtService.generateToken(utilisateur);

                    System.out.println("JWT Token : " + jwtToken);

                } else {
                    System.out.println("❌ Mot de passe incorrect.");
                }
            } else {
                System.out.println("❌ Aucun utilisateur trouvé avec cet email.");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Erreur SQL lors de la connexion : " + e.getMessage());
        }

        return utilisateur;
    }







    public void deconnexion(Utilisateur utilisateur) {
        if (utilisateur != null) {
            System.out.println("📴 Déconnexion réussie. À bientôt, " + utilisateur.getPrenom() + " !");
        } else {
            System.out.println("⚠️ Aucun utilisateur connecté.");
        }
        utilisateur = null; // Invalider l'objet utilisateur
    }


}
