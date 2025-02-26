package org.example.demo1.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.demo1.models.Zone;
import org.example.demo1.utils.DatabaseService;

import java.sql.*;

public class ZoneService {
    private final DatabaseService databaseService;

    public ZoneService() {
        this.databaseService = new DatabaseService();
    }

    // Add a new zone to the database (ID is auto-incremented)
    public void addZone(Zone zone) {
        String query = "INSERT INTO zone (location) VALUES (?)"; // Removed ID field

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, zone.getLocation());
            pstmt.executeUpdate();

            // Get the generated ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                zone.setId(generatedId); // Update Zone object with new ID
                System.out.println("Zone added with ID: " + generatedId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding zone: " + e.getMessage());
        }
    }

    // Modify an existing zone
    public boolean modifyZone(int id, String newLocation) {
        String query = "UPDATE zone SET location = ? WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newLocation);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error modifying zone: " + e.getMessage());
            return false;
        }
    }

    // Delete a zone
    public boolean deleteZone(int id) {
        String query = "DELETE FROM zone WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting zone: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all zones
    public ObservableList<Zone> getAllZones() {
        ObservableList<Zone> zones = FXCollections.observableArrayList();
        String query = "SELECT * FROM zone";

        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                zones.add(new Zone(rs.getInt("id"), rs.getString("location")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving zones: " + e.getMessage());
        }
        return zones;
    }

    // Retrieve a single zone by ID
    public Zone getZoneById(int id) {
        String query = "SELECT * FROM zone WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Zone(rs.getInt("id"), rs.getString("location"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving zone: " + e.getMessage());
        }
        return null;
    }
}
