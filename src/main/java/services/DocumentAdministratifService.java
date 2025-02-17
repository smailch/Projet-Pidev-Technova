package services;
import entities.DocumentAdministratif;
import interfaces.IService;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentAdministratifService implements IService<DocumentAdministratif> {

    @Override
    public void addEntity(DocumentAdministratif documentAdministratif) {
        String req ="INSERT INTO DocumentAdministratif(id,nomDocument, cheminFichier, dateEmission, status, remarque) " +
                "VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, documentAdministratif.getId());
            pst.setString(2, documentAdministratif.getNomDocument());
            pst.setString(3, documentAdministratif.getCheminFichier());
            pst.setString(4, documentAdministratif.getDateEmission());
            pst.setString(5, documentAdministratif.getStatus());
            pst.setString(6, documentAdministratif.getRemarque());
            pst.executeUpdate();
            System.out.println("Document Administratif Ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(DocumentAdministratif documentAdministratif) {
        String req = "DELETE FROM DocumentAdministratif WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, documentAdministratif.getId());
            pst.executeUpdate();
            System.out.println("Document Administratif Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(DocumentAdministratif documentAdministratif) {
        String req = "UPDATE DocumentAdministratif SET nomDocument = ?, cheminFichier = ?, " +
                "dateEmission = ?, status = ?, remarque = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, documentAdministratif.getNomDocument());
            pst.setString(2, documentAdministratif.getCheminFichier());
            pst.setString(3, documentAdministratif.getDateEmission());
            pst.setString(4, documentAdministratif.getStatus());
            pst.setString(5, documentAdministratif.getRemarque());
            pst.setInt(6, documentAdministratif.getId());
            pst.executeUpdate();
            System.out.println("Document Administratif Mis à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<DocumentAdministratif> getAllData() {
        List<DocumentAdministratif> result = new ArrayList<>();
        String req = "SELECT * FROM DocumentAdministratif";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                DocumentAdministratif doc = new DocumentAdministratif();
                doc.setId(rs.getInt(1));
                doc.setNomDocument(rs.getString(2));
                doc.setCheminFichier(rs.getString(3));
                doc.setDateEmission(rs.getString(4));
                doc.setStatus(rs.getString(5));
                doc.setRemarque(rs.getString(6));
                result.add(doc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

}
