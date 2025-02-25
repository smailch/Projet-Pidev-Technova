package services;

import entities.Quartier;
import interfaces.IService;
import tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuartierService implements IService<Quartier> {

    @Override
    public void addEntity(Quartier quartier) {

        try { String req = "INSERT INTO quartier (id, nbLamp, consom_tot) VALUES (?, ?, ?)";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, quartier.getId());
            pst.setInt(2, quartier.getNbLamp());
            pst.setDouble(3, quartier.getConsom_tot());
            pst.executeUpdate();
            System.out.println("Quartier ajouté");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du quartier : " + e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Quartier quartier) {

        try { String req = "DELETE FROM quartier WHERE id = ?";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, quartier.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Quartier supprimé");
            } else {
                System.out.println("Aucun quartier trouvé avec l'ID : " + quartier.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du quartier : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Quartier quartier) {

        try { String req = "UPDATE quartier SET nbLamp = ?, consom_tot = ? WHERE id = ?";

            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, quartier.getNbLamp());
            pst.setDouble(2, quartier.getConsom_tot());
            pst.setInt(3, quartier.getId());
            pst.executeUpdate();
            System.out.println("Quartier mis à jour");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du quartier : " + e.getMessage());
        }
    }

    @Override
    public List<Quartier> getAllData() {
        List<Quartier> result = new ArrayList<>();

        try {String req = "SELECT * FROM quartier";

            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Quartier q = new Quartier();
                q.setId(rs.getInt("id"));
                q.setNbLamp(rs.getInt("nbLamp"));
                q.setConsom_tot(rs.getDouble("consom_tot"));
                result.add(q);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des quartiers : " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean emailExists(String email) {
        return false;
    }
}
