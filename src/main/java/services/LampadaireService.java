package services;
import entities.Quartier;
import entities.Lampadaire;
import interfaces.IService;
import tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LampadaireService implements IService<Lampadaire> {

    @Override
    public void addEntity(Lampadaire lampadaire) {
        String req = "INSERT INTO lampadaire (localisation, etat, consommation, quartier_id) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, lampadaire.getLocalisation());
            pst.setBoolean(2, lampadaire.isEtat());
            pst.setDouble(3, lampadaire.getConsommation());
            pst.setInt(4, lampadaire.getQuartier().getId()); // ID du quartier
            pst.executeUpdate();
            System.out.println("Lampadaire ajouté");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du lampadaire : " + e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Lampadaire lampadaire) {
        String req = "DELETE FROM lampadaire WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, lampadaire.getId()); // Utiliser uniquement l'ID
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Lampadaire supprimé avec succès !");
            } else {
                System.out.println("Aucun lampadaire trouvé avec l'ID : " + lampadaire.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du lampadaire : " + e.getMessage());
        }
    }



    @Override
    public void updateEntity(Lampadaire lampadaire) {
        String req = "UPDATE lampadaire SET localisation = ?, etat = ?, consommation = ?, quartier_id = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, lampadaire.getLocalisation());
            pst.setBoolean(2, lampadaire.isEtat());
            pst.setDouble(3, lampadaire.getConsommation());
            pst.setInt(4, lampadaire.getQuartier().getId()); // Mettre à jour le quartier_id
            pst.setInt(5, lampadaire.getId());
            pst.executeUpdate();
            System.out.println("Lampadaire mis à jour");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du lampadaire : " + e.getMessage());
        }
    }

    @Override
    public List<Lampadaire> getAllData() {
        List<Lampadaire> result = new ArrayList<>();
        String req = "SELECT l.id, l.localisation, l.etat, l.consommation, q.id AS quartier_id, q.nbLamp, q.consom_tot " +
                "FROM lampadaire l " +
                "LEFT JOIN quartier q ON l.quartier_id = q.id"; // Jointure avec la table quartier
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Lampadaire l = new Lampadaire();
                l.setId(rs.getInt("id"));
                l.setLocalisation(rs.getString("localisation"));
                l.setEtat(rs.getBoolean("etat"));
                l.setConsommation(rs.getDouble("consommation"));

                // Créer et initialiser l'objet Quartier
                Quartier quartier = new Quartier();
                quartier.setId(rs.getInt("quartier_id"));
                quartier.setNbLamp(rs.getInt("nbLamp"));
                quartier.setConsom_tot(rs.getDouble("consom_tot"));

                // Associer le quartier au lampadaire
                l.setQuartier(quartier);

                result.add(l);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des lampadaires : " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean emailExists(String email) {
        return false;
    }
}