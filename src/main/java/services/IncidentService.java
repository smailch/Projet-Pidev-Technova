package services;

import entities.Incident;
import entities.Utilisateur;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentService {

    // Create (Ajouter un nouvel incident)
    public boolean ajouterIncident(Incident incident) {
        String req = "INSERT INTO `incident`(`type_incident`, `description`, `localisation`, `statut`, `latitude`, `longitude`, `utilisateur_id`, `image`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
            if (utilisateur == null) return false; // Aucun utilisateur connecté

            stmt.setString(1, incident.getTypeIncident());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getLocalisation());
            stmt.setString(4, "En attente");
            stmt.setDouble(5, incident.getLatitude());
            stmt.setDouble(6, incident.getLongitude());
            stmt.setInt(7, utilisateur.getId());
            stmt.setString(8, incident.getImage());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getNombreIncidentsNonTraites() {
        String query = "SELECT COUNT(*) FROM incident WHERE statut = 'En attente'";
        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean incidentExiste(Incident incident, int utilisateur_id) {
        String req = "SELECT COUNT(*) FROM `incident` WHERE `type_incident` = ? AND `description` = ? AND `localisation` = ? AND `utilisateur_id` = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setString(1, incident.getTypeIncident());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getLocalisation());
            stmt.setInt(4, utilisateur_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // L'incident existe déjà
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // L'incident n'existe pas
    }

    public List<Incident> getIncidentsResolu() {
        List<Incident> incidents = new ArrayList<>();

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) {
            System.out.println("Aucun utilisateur connecté.");
            return incidents;
        }

        // Vérifier si l'utilisateur est un admin
        boolean isAdmin = "Admin".equals(String.valueOf(utilisateur.getRole()));

        // Construire la requête SQL en fonction du rôle
        String req;
        if (isAdmin) {
            req = "SELECT * FROM incident WHERE statut = 'Résolu'"; // L'admin voit tous les incidents résolus
        } else {
            req = "SELECT * FROM incident WHERE statut = 'Résolu' AND utilisateur_id = ?"; // L'utilisateur voit ses incidents résolus
        }

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            if (!isAdmin) {
                stmt.setInt(1, utilisateur.getId()); // Associer l'ID de l'utilisateur à la requête
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incident incident = new Incident(
                            rs.getInt("id"),
                            rs.getString("type_incident"),
                            rs.getString("description"),
                            rs.getString("localisation"),
                            rs.getString("statut"),
                            rs.getTimestamp("date_signalement"),
                            rs.getInt("service_affecte"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getString("image"),
                            rs.getTimestamp("date_resolution")

                            );
                    System.out.println("Incident ID: " + incident.getId() + ", Date Resolution: " + incident.getDateResolution());

                    incidents.add(incident);
                }

            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des incidents résolus : " + e.getMessage());
        }

        return incidents;
    }



    public void ajouterTempsResolution(Incident incident) {
        String req = "UPDATE incident SET date_resolution = ? WHERE id = ?";
        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setTimestamp(1, incident.getDateResolution());
            stmt.setInt(2, incident.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Date de résolution mise à jour avec succès !");
            } else {
                System.out.println("Aucun incident mis à jour !");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la date de résolution : " + e.getMessage());
        }
    }

    // Read (Afficher la liste de tous les incidents)
    public List<Incident> afficherIncidents() {
        List<Incident> incidents = new ArrayList<>();

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) {
            System.out.println("Aucun utilisateur connecté.");
            return incidents;
        }

        // Vérifier si l'utilisateur est un admin
        boolean isAdmin = (String.valueOf(utilisateur.getRole()).equals("Admin"));

        // Construire la requête SQL en fonction du rôle
        String req;
        if (isAdmin) {
            req = "SELECT * FROM incident"; // L'admin voit tous les incidents
        } else {
            req = "SELECT * FROM incident WHERE utilisateur_id = ?"; // Un utilisateur normal voit seulement ses incidents
        }

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            if (!isAdmin) {
                stmt.setInt(1, utilisateur.getId()); // Associer l'ID de l'utilisateur à la requête
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incident incident = new Incident(
                            rs.getInt("id"),
                            rs.getString("type_incident"),
                            rs.getString("description"),
                            rs.getString("localisation"),
                            rs.getString("statut"),
                            rs.getTimestamp("date_signalement"),
                            rs.getInt("service_affecte"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getString("image")

                    );
                    incidents.add(incident);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur d'affichage : " + e.getMessage());
        }

        return incidents;
    }

    public List<Incident> afficherIncidentsadmine() {
        List<Incident> incidents = new ArrayList<>();

        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) {
            System.out.println("Aucun utilisateur connecté.");
            return incidents;
        }

        // Vérifier si l'utilisateur est un admin
        boolean isAdmin = (String.valueOf(utilisateur.getRole()).equals("Admin"));

        // Construire la requête SQL en fonction du rôle
        String req;
        if (isAdmin) {
            req = "SELECT * FROM incident WHERE statut IN ('En cours', 'En attente')"; // L'admin voit les incidents en cours et en attente
        } else {
            req = "SELECT * FROM incident WHERE utilisateur_id = ? AND statut IN ('En cours', 'En attente')"; // Un utilisateur normal voit ses incidents en cours et en attente
        }

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            if (!isAdmin) {
                stmt.setInt(1, utilisateur.getId()); // Associer l'ID de l'utilisateur à la requête
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incident incident = new Incident(
                            rs.getInt("id"),
                            rs.getString("type_incident"),
                            rs.getString("description"),
                            rs.getString("localisation"),
                            rs.getString("statut"),
                            rs.getTimestamp("date_signalement"),
                            rs.getInt("service_affecte"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getString("image")
                    );
                    incidents.add(incident);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur d'affichage : " + e.getMessage());
        }

        return incidents;
    }
    public List<Incident> getIncidentsResolusPourCitoyen(int utilisateurId) {
        List<Incident> incidents = new ArrayList<>();

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM incident WHERE utilisateur_id = ? AND statut = 'Résolu'")) {

            stmt.setInt(1, utilisateurId);  // Paramétrer l'ID de l'utilisateur (citoyen)
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Récupérer toutes les informations de l'incident depuis le ResultSet
                int id = rs.getInt("id");
                String typeIncident = rs.getString("type_incident");  // Type d'incident
                String description = rs.getString("description");
                String localisation = rs.getString("localisation");
                String statut = rs.getString("statut");
                Timestamp dateSignalement = rs.getTimestamp("date_signalement");
                int serviceAffecte = rs.getInt("service_affecte");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                String image = rs.getString("image");
                Timestamp dateResolution = rs.getTimestamp("date_resolution");// Chemin de l'image associée à l'incident

                // Créer l'objet Incident avec toutes les informations récupérées
                Incident incident = new Incident(id, typeIncident, description, localisation, statut, dateSignalement,
                        serviceAffecte, latitude, longitude, image,dateResolution);

                // Ajouter l'incident à la liste
                incidents.add(incident);
            }

            // Afficher un message dans la console pour vérifier si des incidents sont récupérés
            System.out.println("Incidents résolus récupérés pour l'utilisateur " + utilisateurId + " : " + incidents.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return incidents;
    }

    // Récupérer tous les noms de service
    public List<String> getAllServiceNames() {
        List<String> serviceNames = new ArrayList<>();
        String req = "SELECT nom_service FROM serviceintervention";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                serviceNames.add(rs.getString("nom_service"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des services : " + e.getMessage());
        }

        return serviceNames;
    }

    // Affecter un service à un incident en utilisant le nom du service
    public boolean affecterServiceParNom(int incidentId, String nomService) {
        String reqService = "SELECT id FROM serviceintervention WHERE nom_service = ?";
        int serviceId = -1;

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(reqService)) {
            stmt.setString(1, nomService);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    serviceId = rs.getInt("id");
                } else {
                    System.out.println("Aucun service trouvé avec ce nom.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du service : " + e.getMessage());
            return false;
        }

        String reqUpdate = "UPDATE incident SET service_affecte = ? WHERE id = ?";
        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(reqUpdate)) {
            stmt.setInt(1, serviceId);
            stmt.setInt(2, incidentId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affectation du service : " + e.getMessage());
            return false;
        }
    }

    public void modifierIncident(Incident incident) {
        String query = "UPDATE incident SET type_incident = ?, description = ?, localisation = ?, statut = ?, " +
                "date_signalement = ?, image = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(query)) {            stmt.setString(1, incident.getTypeIncident());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getLocalisation());
            stmt.setString(4, incident.getStatut());
            stmt.setTimestamp(5, incident.getDateSignalement());
            stmt.setString(6, incident.getImage()); ;
            stmt.setInt(7, incident.getId());  // Use the ID to identify the incident

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierStatusIncident(Incident incident) {
        String query = "UPDATE incident SET statut = ? WHERE id = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, incident.getStatut()); // Mise à jour du statut
            stmt.setInt(2, incident.getId()); // Condition WHERE avec l'ID de l'incident

            int rowsUpdated = stmt.executeUpdate(); // Exécuter la mise à jour

            if (rowsUpdated > 0) {
                System.out.println("Statut mis à jour avec succès !");
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifie l'ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void CitoyenmodifierIncident(Incident incident) {
        String query = "UPDATE incident SET type_incident = ?, description = ?, localisation = ?," +
                "date_signalement = ?, image = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(query)) {            stmt.setString(1, incident.getTypeIncident());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getLocalisation());
            stmt.setTimestamp(4, incident.getDateSignalement());
            stmt.setString(5, incident.getImage()); ;
            stmt.setInt(6, incident.getId());  // Use the ID to identify the incident

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Delete (Supprimer un incident)
    public void supprimerIncident(Incident incident) {
        String req = "DELETE FROM incident WHERE id = ?";

        try (PreparedStatement pst = MyConnection.getConnection().prepareStatement(req)) {
            pst.setInt(1, incident.getId());
            pst.executeUpdate();
            System.out.println("Incident supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur de suppression : " + e.getMessage());
        }
    }
    public void supprimerHistoriqueIncident(int incidentId) {
        String req = "DELETE FROM incident WHERE id = ?";
        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setInt(1, incidentId);
            stmt.executeUpdate();
            System.out.println("Incident supprimé avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // Recherche d'un incident par son ID
    public Incident getIncidentById(int id) {
        String req = "SELECT * FROM incident WHERE id = ?";
        Incident incident = null;

        try (PreparedStatement pst = MyConnection.getConnection().prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    incident = new Incident(
                            rs.getInt("id"),
                            rs.getString("type_incident"),
                            rs.getString("description"),
                            rs.getString("localisation"),
                            rs.getString("statut"),
                            rs.getTimestamp("date_signalement"),
                            rs.getInt("service_affecte"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getString("image")  // Fetch the image field from the database
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de recherche : " + e.getMessage());
        }

        return incident;
    }

    public String getServiceNameById(int serviceId) {
        String serviceName = null;
        String req = "SELECT nom_service FROM serviceintervention WHERE id_service = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    serviceName = rs.getString("nom_service");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom du service : " + e.getMessage());
        }

        return serviceName;
    }
    // Méthode pour récupérer tous les incidents
    public List<Incident> getAllIncidents() {
        return afficherIncidents();
    }
    public List<Incident> GetAllIncidents() {
        List<Incident> incidents = new ArrayList<>();
        String query = "SELECT * FROM incident";

        try (Statement stmt = MyConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Incident incident = new Incident();
                incident.setId(rs.getInt("id"));
                incident.setStatut(rs.getString("statut"));
                incident.setutilisateurId(rs.getInt("utilisateur_id")); // Assurez-vous que la colonne existe en BDD
                incidents.add(incident);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return incidents;
    }

}
