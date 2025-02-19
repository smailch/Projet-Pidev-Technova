package services;

import entities.Quartier;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuartierService {

    // Method to add a new Quartier
    public void addEntity(Quartier quartier) {
        String req = "INSERT INTO quartier (nom, nbLamp, consom_tot) VALUES (?, ?, ?)";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setString(1, quartier.getNom());
            pst.setInt(2, quartier.getNbLamp());
            pst.setDouble(3, quartier.getConsomTot());
            pst.executeUpdate();
            System.out.println("Quartier ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du quartier : " + e.getMessage());
        }
    }

    // Method to delete a Quartier by ID
    public void deleteEntity(int id) {
        String req = "DELETE FROM quartier WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Quartier supprimé avec succès !");
            } else {
                System.out.println("Aucun quartier trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du quartier : " + e.getMessage());
        }
    }

    // Method to update a Quartier
    public void updateEntity(Quartier quartier) {
        String req = "UPDATE quartier SET nom = ?, nbLamp = ?, consom_tot = ? WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setString(1, quartier.getNom());
            pst.setInt(2, quartier.getNbLamp());
            pst.setDouble(3, quartier.getConsomTot());
            pst.setInt(4, quartier.getId());
            pst.executeUpdate();
            System.out.println("Quartier mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du quartier : " + e.getMessage());
        }
    }

    // Method to retrieve all Quartiers
    public List<Quartier> getAllData() {
        List<Quartier> result = new ArrayList<>();
        String req = "SELECT * FROM quartier";
        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Quartier quartier = new Quartier(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("nbLamp"),
                        rs.getDouble("consom_tot")
                );
                result.add(quartier);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des quartiers : " + e.getMessage());
        }
        return result;
    }

    // Method to retrieve a Quartier by name
    public Quartier getQuartierByNom(String nom) {
        String req = "SELECT * FROM quartier WHERE nom = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setString(1, nom);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Quartier(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("nbLamp"),
                        rs.getDouble("consom_tot")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du quartier par nom : " + e.getMessage());
        }
        return null;
    }
}