package org.example.demo1.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.demo1.models.Camion_Collecte;
import org.example.demo1.utils.DatabaseService;

import java.sql.*;

public class CamionCollecteService {
    private final DatabaseService databaseService;

    public CamionCollecteService() {
        this.databaseService = new DatabaseService();
    }

    /**
     * Add a new camion to the database.
     *
     * @param camion The camion to add.
     */
    public void addCamion(Camion_Collecte camion) {
        String query = "INSERT INTO camion_collecte (capacite_max, statut, zone_id) VALUES (?, ?, ?)";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, camion.getCapacite_max());
            pstmt.setString(2, camion.getStatut());
            pstmt.setInt(3, camion.getZone_id());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                camion.setId(generatedKeys.getInt(1));
                System.out.println("Camion added successfully with ID: " + camion.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error adding camion: " + e.getMessage());
        }
    }

    /**
     * Modify an existing Camion_Collecte.
     *
     * @param id         The ID of the camion to modify.
     * @param capacite_max The new maximum capacity.
     * @param statut     The new status.
     * @param zone_id    The new zone ID.
     * @return True if the modification was successful; false otherwise.
     */
    public boolean modifyCamion(int id, Double capacite_max, String statut, int zone_id) {
        String query = "UPDATE camion_collecte SET capacite_max = ?, statut = ?, zone_id = ? WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, capacite_max);
            pstmt.setString(2, statut);
            pstmt.setInt(3, zone_id);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error modifying camion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a Camion_Collecte.
     *
     * @param id The ID of the camion to delete.
     * @return True if the deletion was successful; false otherwise.
     */
    public boolean deleteCamion(int id) {
        String query = "DELETE FROM camion_collecte WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting camion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve all Camion_Collecte records.
     *
     * @return A list of all camions.
     */
    public ObservableList<Camion_Collecte> getAllCamions() {
        ObservableList<Camion_Collecte> camions = FXCollections.observableArrayList();
        String query = "SELECT * FROM camion_collecte";

        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                camions.add(new Camion_Collecte(
                        rs.getInt("id"),
                        rs.getDouble("capacite_max"),
                        rs.getString("statut"),
                        rs.getInt("zone_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving camions: " + e.getMessage());
        }
        return camions;
    }

    /**
     * Retrieve Camion_Collecte records by zone_id.
     *
     * @param zoneId The ID of the zone.
     * @return A list of camions in the specified zone.
     */
    public ObservableList<Camion_Collecte> getCamionsByZone(int zoneId) {
        ObservableList<Camion_Collecte> camions = FXCollections.observableArrayList();
        String query = "SELECT * FROM camion_collecte WHERE zone_id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, zoneId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                camions.add(new Camion_Collecte(
                        rs.getInt("id"),
                        rs.getDouble("capacite_max"),
                        rs.getString("statut"),
                        rs.getInt("zone_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving camions by zone: " + e.getMessage());
        }
        return camions;
    }
}