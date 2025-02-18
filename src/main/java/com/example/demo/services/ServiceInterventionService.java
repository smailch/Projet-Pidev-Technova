package com.example.demo.services;

import com.example.demo.entities.Incident;
import com.example.demo.entities.ServiceIntervention;
import com.example.demo.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceInterventionService {

    // Méthode pour ajouter un service
    public boolean ajouterService(ServiceIntervention service) {
        String sql = "INSERT INTO serviceintervention(nom_service, type_intervention, zone_intervention) VALUES(?, ?, ?)";
        try (Connection conn = MyConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("Connection is not open!");
                return false;
            }

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

        try (Statement st = MyConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
        try (Connection conn = MyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = MyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = MyConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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


}
