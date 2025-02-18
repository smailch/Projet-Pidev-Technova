package com.example.demo.services;

import com.example.demo.entities.Incident;
import com.example.demo.entities.Utilisateur;
import com.example.demo.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentService {

    // Create (Ajouter un nouvel incident)
    public void ajouterIncident(Incident incident) {
        // Récupérer l'ID de l'utilisateur connecté depuis la session
        Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        int utilisateur_id= utilisateur != null ? utilisateur.getId() : -1; // Vérification si l'utilisateur est connecté

        // Si l'utilisateur n'est pas connecté, afficher un message d'erreur et sortir
        if (utilisateur_id == -1) {
            System.out.println("Erreur: L'utilisateur n'est pas connecté.");
            return; // Tu peux aussi afficher une alerte ou autre mécanisme d'erreur
        }
        String statut = "En attente"; // Définition du statut par défaut

        String req = "INSERT INTO `incident`(`type_incident`, `description`, `localisation`, `statut`, `latitude`, `longitude`, `image`, `utilisateur_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setString(1, incident.getTypeIncident());
            stmt.setString(2, incident.getDescription());
            stmt.setString(3, incident.getLocalisation());
            stmt.setString(4, statut); // Toujours "Non résolu"
            stmt.setDouble(5, incident.getLatitude());
            stmt.setDouble(6, incident.getLongitude());
            stmt.setString(7, incident.getImage());
            stmt.setInt(8, utilisateur_id);

            stmt.executeUpdate();
            System.out.println("Incident ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
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
}
