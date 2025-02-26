package org.example.demo1.services;

import org.example.demo1.models.Poubelle_Intelligente;
import org.example.demo1.utils.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PoubelleIntelligenteService {

    private final DatabaseService databaseService;

    public PoubelleIntelligenteService() {
        this.databaseService = new DatabaseService();
    }

    public void addPoubelle(Poubelle_Intelligente poubelle) {
        String query = "INSERT INTO poubelle_intelligente (type_dechets, niveau_remplissage, localisation, zone_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, poubelle.getType_dechets());
            pstmt.setDouble(2, poubelle.getNiveau_remplissage());
            pstmt.setString(3, poubelle.getLocalisation());
            pstmt.setInt(4, poubelle.getZoneId());
            pstmt.executeUpdate();

            System.out.println("Poubelle added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding poubelle: " + e.getMessage());
        }
    }

    public List<Poubelle_Intelligente> getAllPoubelles() {
        List<Poubelle_Intelligente> poubelles = new ArrayList<>();
        String query = "SELECT * FROM poubelle_intelligente";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Poubelle_Intelligente poubelle = new Poubelle_Intelligente();
                poubelle.setId(rs.getInt("id"));
                poubelle.setType_dechets(rs.getString("type_dechets"));
                poubelle.setNiveau_remplissage(rs.getDouble("niveau_remplissage"));
                poubelle.setLocalisation(rs.getString("localisation"));
                poubelle.setZoneId(rs.getInt("zone_id"));
                poubelles.add(poubelle);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving poubelles: " + e.getMessage());
        }

        return poubelles;
    }

    public void deletePoubelle(int id) {
        String query = "DELETE FROM poubelle_intelligente WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Poubelle with id " + id + " was deleted successfully!");
            } else {
                System.out.println("No poubelle found with id " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting poubelle: " + e.getMessage());
        }
    }
    public void updatePoubelle(Poubelle_Intelligente poubelle) {
        String query = "UPDATE poubelle_intelligente SET type_dechets = ?, niveau_remplissage = ?, localisation = ?, zone_id = ? WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, poubelle.getType_dechets());
            pstmt.setDouble(2, poubelle.getNiveau_remplissage());
            pstmt.setString(3, poubelle.getLocalisation());
            pstmt.setInt(4, poubelle.getZoneId());
            pstmt.setInt(5, poubelle.getId());

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

            System.out.println("Poubelle updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating poubelle: " + e.getMessage());
        }
    }
}