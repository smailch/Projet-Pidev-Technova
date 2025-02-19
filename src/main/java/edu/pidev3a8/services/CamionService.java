package edu.pidev3a8.services;

import edu.pidev3a8.interfaces.IService;
import edu.pidev3a8.tools.MyConnection;
import edu.pidev3a8.entities.Camion_Collecte;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class CamionService implements IService<Camion_Collecte> {
    @Override
    public void addEntity(Camion_Collecte camion){
        String req= "INSERT INTO `camion_collecte`(`id`, `capacite_max`, `zone_assigne`, `statut`) " +
                "VALUES (?,?,?,?)";
        try {
            PreparedStatement pst= MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, camion.getId());
            pst.setDouble(2, camion.getCapacite_max());
            pst.setString(3, camion.getZone_assigne());
            pst.setString(4, camion.getStatut());
            pst.executeUpdate();
            System.out.println("Camion Ajouté!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
    }
    @Override
    public void updateEntity(Camion_Collecte camion) {
        String req = "UPDATE camion_collecte SET capacite_max = ?, zone_assigne = ?, " +
                "statut = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setDouble(1, camion.getCapacite_max());
            pst.setString(2, camion.getZone_assigne());
            pst.setString(3, camion.getStatut());
            pst.setInt(4, camion.getId());
            pst.executeUpdate();
            System.out.println("Camion Mis à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void deleteEntity(Camion_Collecte camion) {
        String req = "DELETE FROM camion_collecte WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, camion.getId());
            pst.executeUpdate();
            System.out.println("Camion Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Camion_Collecte> getAllData() {
        List<Camion_Collecte> result = new ArrayList<>();
        String req = "SELECT * FROM camion_collecte";
        try {
            Statement st=MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Camion_Collecte camion = new Camion_Collecte();
                camion.setId(rs.getInt("id"));
                camion.setCapacite_max(rs.getDouble("capacite_max"));
                camion.setStatut(rs.getString("statut"));
                camion.setZone_assigne(rs.getString("zone_assigne"));
                result.add(camion);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return result;
    }
}
