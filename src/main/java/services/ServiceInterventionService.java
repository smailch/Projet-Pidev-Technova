package services;

import entities.Incident;
import entities.ServiceIntervention;
import tools.MyConnection;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import entities.Utilisateur;

import java.util.Properties;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceInterventionService {

    // Méthode pour ajouter un service
    public boolean ajouterService(ServiceIntervention service) throws SQLException {
        String sql = "INSERT INTO serviceintervention(nom_service, type_intervention, zone_intervention) VALUES(?, ?, ?)";
        try  {

            PreparedStatement pst=MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, service.getNomService());
            pst.setString(2, service.getTypeInterventionString());
            pst.setString(3, service.getZoneIntervention());

            int rowsInserted = pst.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour récupérer tous les services
    public List<ServiceIntervention> afficherService() {
        List<ServiceIntervention> services = new ArrayList<>();
        String sql = "SELECT * FROM serviceintervention";

        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ServiceIntervention service = new ServiceIntervention(
                        rs.getInt("id"),
                        rs.getString("nom_service"),
                        rs.getString("type_intervention"),
                        rs.getString("zone_intervention")
                );
                services.add(service);
            }

            // Ajoutez un log pour vérifier les services récupérés
            System.out.println("Nombre de services récupérés: " + services.size());
            services.forEach(service -> System.out.println(service));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }


    // Méthode pour récupérer un service par ID
    public ServiceIntervention getServiceById(int id) {
        String sql = "SELECT * FROM serviceintervention WHERE id = ?";
        try  {
            PreparedStatement stmt=MyConnection.getInstance().getCnx().prepareStatement(sql);

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ServiceIntervention(
                            rs.getInt("id"),
                            rs.getString("nom_service"),
                            rs.getString("type_intervention"),
                            rs.getString("zone_intervention")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour supprimer un service par ID
    public boolean supprimerService(int id) {
        String sql = "DELETE FROM serviceintervention WHERE id = ?";
        try  {
            PreparedStatement stmt=MyConnection.getInstance().getCnx().prepareStatement(sql);

            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour mettre à jour un service
    public boolean updateService(int id, String nomService, String typeIntervention, String zoneIntervention) {
        String sql = "UPDATE serviceintervention SET nom_service = ?, type_intervention = ?, zone_intervention = ? WHERE id = ?";
        try  {
            PreparedStatement stmt=MyConnection.getInstance().getCnx().prepareStatement(sql);

            stmt.setString(1, nomService);
            stmt.setString(2, typeIntervention);
            stmt.setString(3, zoneIntervention);
            stmt.setInt(4, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour modifier un service (Redondant avec updateService)
    public boolean modifierService(ServiceIntervention selectedService) {
        return updateService(selectedService.getId(), selectedService.getNomService(),
                selectedService.getTypeIntervention(), selectedService.getZoneIntervention());
    }
    public List<ServiceIntervention> getAllservices() {
        return afficherService();
    }
    public static String calculerDureeResolution(Incident incident) {
        // Vérifier que les dates sont définies
        Timestamp declaration = incident.getDateSignalement();
        Timestamp resolution = incident.getDateResolution();

        if (declaration == null || resolution == null) {
            return "Durée non définie";
        }

        // Calculer la différence en millisecondes
        long diffMillis = resolution.getTime() - declaration.getTime();

        // Convertir la différence en heures, minutes et secondes
        long diffHours = diffMillis / (60 * 60 * 1000);
        long diffMinutes = (diffMillis / (60 * 1000)) % 60;
        long diffSeconds = (diffMillis / 1000) % 60;

        return (diffHours-1)+ "h " + diffMinutes + "m " + diffSeconds + "s";
    }
    public static void envoyerEmailIncidentResolut(Utilisateur utilisateur, Incident incident) {
        String to = utilisateur.getEmail();
        String from = "mouradmissaoui76@gmail.com";
        final String username = "mouradmissaoui76@gmail.com";
        final String password = "hgrq uqtz wntw ddai"; // ⚠️ Stocker dans un fichier sécurisé !

        String host = "smtp.gmail.com";
        String imagePath = "src/main/resources/assets/images/logo.png";
        String dureeResolution = calculerDureeResolution(incident);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("✅ Incident résolu - " + incident.getDescription());

            // Corps HTML de l'email
            String emailContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; background-color: #f4f4f4; border-radius: 10px;'>"
                    + "<div style='text-align: center;'>"
                    + "<img src='cid:imageLogo' alt='CiviSmart Logo' style='width: 120px; margin-bottom: 20px;'>"
                    + "</div>"
                    + "<h2 style='color: #28a745; text-align: center;'>Votre incident a été résolu ✅</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Bonjour <b>" + utilisateur.getNom() + "</b>,</p>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "L'incident <b>\"" + incident.getDescription() + "\"</b> que vous avez signalé a été traité avec succès et est désormais marqué comme <b>résolu</b>."
                    + "</p>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "Détails de l’incident : <br>"
                    + "<b>Type :</b> " + incident.getTypeIncident() + "<br>"
                    + "<b>Description :</b> " + incident.getDescription() + "<br>"
                    + "<b>Localisation :</b> " + incident.getLocalisation()
                    + "</p>"
                    + "⏳ Durée de résolution : " + dureeResolution + "\n\n"
                    + "<div style='text-align: center; margin: 20px;'>"
                    + "<a href='https://www.civismart.com/incident?id=" + incident.getId() + "' style='background-color: #28a745; color: #fff; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 16px;'>"
                    + "Consulter mon incident</a>"
                    + "</div>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>"
                    + "Merci d'avoir utilisé <b>CiviSmart</b>. Si vous avez des questions, contactez-nous !"
                    + "<br>&copy; 2025 CiviSmart - Tous droits réservés.</p>"
                    + "</div>";

            // Création de la partie HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContent, "text/html; charset=utf-8");

            // Ajout du logo en pièce jointe
            MimeBodyPart imagePart = new MimeBodyPart();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imagePart.attachFile(imageFile);
                imagePart.setContentID("<imageLogo>");
                imagePart.setDisposition(MimeBodyPart.INLINE);
            } else {
                System.out.println("❌ Erreur : L'image du logo est introuvable à " + imagePath);
                return;
            }

            // Assemblage des parties
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            multipart.addBodyPart(imagePart);
            message.setContent(multipart);

            // Envoi du mail
            Transport.send(message);
            System.out.println("📧 Email envoyé avec succès à " + utilisateur.getEmail());

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }
    public static void EmailTravaux(String destinataire, String sujet, String messageHtml) {
        String from = "mouradmissaoui76@gmail.com";
        final String username = "mouradmissaoui76@gmail.com";
        final String password = "hgrq uqtz wntw ddai"; // ⚠️ Stocke ça dans un fichier sécurisé !

        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
            message.setSubject(sujet);
            message.setContent(messageHtml, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("📧 Email envoyé à " + destinataire);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi du mail : " + e.getMessage());
        }
    }

    public List<Incident> getIncidentsByService(int serviceId) {
        List<Incident> incidents = new ArrayList<>();
        String sql="SELECT * FROM incident WHERE service_affecte = ? AND statut = 'En cours'";
        try {

            PreparedStatement stmt=MyConnection.getInstance().getCnx().prepareStatement(sql);

            stmt.setInt(1, serviceId);  // Paramétrer l'ID du service
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String localisation = rs.getString("localisation");
                Timestamp dateSignalement = rs.getTimestamp("date_signalement");

                // Créer l'objet Incident avec les données récupérées
                Incident incident = new Incident(id, description, dateSignalement, localisation);
                incidents.add(incident);
            }

            // Afficher un message dans la console pour vérifier si des incidents sont récupérés
            System.out.println("Incidents récupérés pour le service " + serviceId + " : " + incidents.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return incidents;
    }




}
