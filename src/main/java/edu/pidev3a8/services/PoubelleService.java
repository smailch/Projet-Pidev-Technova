package edu.pidev3a8.services;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import edu.pidev3a8.entities.Poubelle_Intelligente;
import edu.pidev3a8.interfaces.IService;
import edu.pidev3a8.tools.MyConnection;

public class PoubelleService implements IService<Poubelle_Intelligente> {
    @Override
    public void addEntity(Poubelle_Intelligente poubelle){
        String req = "INSERT INTO `poubelle_intelligente`(`id`, `type_dechets`, `niveau_remplissage`,`localisation`) " +
                "VALUES (?,?,?,?)";
        try {
            PreparedStatement pst= MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, poubelle.getId());
            pst.setString(2, poubelle.getType_dechets());
            pst.setDouble(3, poubelle.getNiveau_remplissage());
            pst.setString(4, poubelle.getLocalisation());
            pst.executeUpdate();
            System.out.println("Poubelle Ajouté!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
    }
    @Override
    public void updateEntity(Poubelle_Intelligente p) {
        String req = "UPDATE poubelle_intelligente SET type_dechets = ?, niveau_remplissage = ?, " +
                "localisation = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, p.getType_dechets());
            pst.setDouble(2, p.getNiveau_remplissage());
            pst.setString(3, p.getLocalisation());
            pst.setInt(4, p.getId());
            pst.executeUpdate();
            System.out.println("Poubelle Mis à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Poubelle_Intelligente poubelle) {
        deleteEntityWithID(poubelle.getId());
    }

    public void deleteEntityWithID(int id) {
        String req = "DELETE FROM poubelle_intelligente WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Poubelle Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Poubelle_Intelligente> getAllData() {
        List<Poubelle_Intelligente> result = new ArrayList<>();
        String req = "SELECT * FROM poubelle_intelligente";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Poubelle_Intelligente poubelle = new Poubelle_Intelligente();
                poubelle.setId(rs.getInt("id"));
                poubelle.setType_dechets(rs.getString("type_dechets"));
                poubelle.setNiveau_remplissage(rs.getDouble("niveau_remplissage"));
                poubelle.setLocalisation(rs.getString("localisation"));
                result.add(poubelle);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return result;
    }
}
