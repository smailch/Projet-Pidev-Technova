package services;

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

        try {
            String req = "INSERT INTO lampadaire (id, localisation, etat, consommation) VALUES (?, ?, ?, ?)";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, lampadaire.getId());
            pst.setString(2, lampadaire.getLocalisation());
            pst.setBoolean(3, lampadaire.isEtat());
            pst.setDouble(4, lampadaire.getConsommation());
            pst.executeUpdate();
            System.out.println("Lampadaire ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Lampadaire lampadaire) {

        try { String req = "DELETE FROM lampadaire WHERE id = ?" + lampadaire.getId();

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, lampadaire.getId());
            pst.executeUpdate();
            System.out.println("Lampadaire supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void updateEntity(Lampadaire lampadaire) {

        try { String req = "UPDATE lampadaire SET localisation = ?, etat = ?, consommation = ? WHERE id = ?";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, lampadaire.getLocalisation());
            pst.setBoolean(2, lampadaire.isEtat());
            pst.setDouble(3, lampadaire.getConsommation());
            pst.setInt(4, lampadaire.getId());
            pst.executeUpdate();
            System.out.println("Lampadaire mis à jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Lampadaire> getAllData() {
        List<Lampadaire> result = new ArrayList<>();

        try {
            String req = "SELECT * FROM lampadaire";

            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Lampadaire l = new Lampadaire();
                l.setId(rs.getInt("id"));
                l.setLocalisation(rs.getString("localisation"));
                l.setEtat(rs.getBoolean("etat"));
                l.setConsommation(rs.getDouble("consommation"));
                result.add(l);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}