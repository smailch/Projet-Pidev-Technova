package services;

import entities.Lampadaire;
import entities.Quartier;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LampadaireService {

    // Method to add a new Lampadaire
    public void addEntity(Lampadaire lampadaire) {
        String req = "INSERT INTO lampadaire (localisation, etat, consommation, quartier_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setString(1, lampadaire.getLocalisation());
            pst.setBoolean(2, lampadaire.isEtat());
            pst.setDouble(3, lampadaire.getConsommation());
            pst.setInt(4, lampadaire.getQuartier().getId());
            pst.executeUpdate();
            System.out.println("Lampadaire ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du lampadaire : " + e.getMessage());
        }
    }

    // Method to delete a Lampadaire
    public void deleteEntity(Lampadaire lampadaire) {
        String req = "DELETE FROM lampadaire WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setInt(1, lampadaire.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Lampadaire supprimé avec succès !");
            } else {
                System.out.println("Aucun lampadaire trouvé avec l'ID : " + lampadaire.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du lampadaire : " + e.getMessage());
        }
    }

    // Method to update a Lampadaire
    public void updateEntity(Lampadaire lampadaire) {
        String req = "UPDATE lampadaire SET localisation = ?, etat = ?, consommation = ?, quartier_id = ? WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req)) {
            pst.setString(1, lampadaire.getLocalisation());
            pst.setBoolean(2, lampadaire.isEtat());
            pst.setDouble(3, lampadaire.getConsommation());
            pst.setInt(4, lampadaire.getQuartier().getId());
            pst.setInt(5, lampadaire.getId());
            pst.executeUpdate();
            System.out.println("Lampadaire mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du lampadaire : " + e.getMessage());
        }
    }

    // Method to retrieve all Lampadaires
    public List<Lampadaire> getAllData() {
        List<Lampadaire> result = new ArrayList<>();
        String req = "SELECT l.id, l.localisation, l.etat, l.consommation, q.id AS quartier_id, q.nom, q.nbLamp, q.consom_tot " +
                "FROM lampadaire l " +
                "LEFT JOIN quartier q ON l.quartier_id = q.id";
        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Lampadaire l = new Lampadaire();
                l.setId(rs.getInt("id"));
                l.setLocalisation(rs.getString("localisation"));
                l.setEtat(rs.getBoolean("etat"));
                l.setConsommation(rs.getDouble("consommation"));

                Quartier quartier = new Quartier(
                        rs.getInt("quartier_id"),
                        rs.getString("nom"),
                        rs.getInt("nbLamp"),
                        rs.getDouble("consom_tot")
                );
                l.setQuartier(quartier);

                result.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lampadaires : " + e.getMessage());
        }
        return result;
    }
}