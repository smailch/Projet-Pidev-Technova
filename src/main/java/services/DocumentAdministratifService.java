package services;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import entities.DocumentAdministratif;
import interfaces.IService;
import tools.MyConnection;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentAdministratifService implements IService<DocumentAdministratif> {

    @Override
    public void addEntity(DocumentAdministratif documentAdministratif) {
        String req ="INSERT INTO DocumentAdministratif(id, id_dossier, nom_document, chemin_fichier, date_emission, status, remarque) " +
                "VALUES(?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, documentAdministratif.getId());
            pst.setInt(2, documentAdministratif.getIdDossier());
            pst.setString(3, documentAdministratif.getNomDocument());
            pst.setString(4, documentAdministratif.getCheminFichier());
            pst.setString(5, documentAdministratif.getDateEmission());
            pst.setString(6, documentAdministratif.getStatus());
            pst.setString(7, documentAdministratif.getRemarque());
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
        String req = "UPDATE DocumentAdministratif SET id_dossier = ?, nom_document = ?, chemin_fichier = ?, " +
                "date_emission = ?, status = ?, remarque = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, documentAdministratif.getIdDossier());
            pst.setString(2, documentAdministratif.getNomDocument());
            pst.setString(3, documentAdministratif.getCheminFichier());
            pst.setString(4, documentAdministratif.getDateEmission());
            pst.setString(5, documentAdministratif.getStatus());
            pst.setString(6, documentAdministratif.getRemarque());
            pst.setInt(7, documentAdministratif.getId());
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
                doc.setIdDossier(rs.getInt(2));
                doc.setNomDocument(rs.getString(3));
                doc.setCheminFichier(rs.getString(4));
                doc.setDateEmission(rs.getString(5));
                doc.setStatus(rs.getString(6));
                doc.setRemarque(rs.getString(7));
                result.add(doc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

}
