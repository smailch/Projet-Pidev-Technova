package services;

import entities.DocumentAdministratif;
import entities.Validation;
import interfaces.IService;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValidationService implements IService<Validation> {

    @Override
    public void addEntity(Validation validation) {
        String req = "INSERT INTO Validation(idDocument, statut, datevalidation, remarque) " +
                "VALUES(?,?,?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, validation.getIdDocument());
            pst.setString(2, validation.getStatut());
            pst.setString(3, validation.getDateValidation());
            pst.setString(4, validation.getRemarque());
            pst.executeUpdate();
            System.out.println("Validation Ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(Validation validation) {
        String req = "DELETE FROM Validation WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, validation.getId());
            pst.executeUpdate();
            System.out.println("Validation Supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(Validation validation) {
        String req = "UPDATE Validation SET idDocument = ?, statut = ?, dateValidation = ?, remarque = ? " +
                "WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, validation.getIdDocument());
            pst.setString(2, validation.getStatut());
            pst.setString(3, validation.getDateValidation());
            pst.setString(4, validation.getRemarque());
            pst.setInt(5, validation.getId());
            pst.executeUpdate();
            System.out.println("Validation Mise à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Validation> getAllData() {
        List<Validation> result = new ArrayList<>();
        String req = "SELECT * FROM Validation";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Validation validation = new Validation();
                validation.setId(rs.getInt(1));
                validation.setIdDocument(rs.getInt(2));
                validation.setStatut(rs.getString(3));
                validation.setDateValidation(rs.getString(4));
                validation.setRemarque(rs.getString(5));
                result.add(validation);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }



    @Override
    public boolean emailExists(String email) {
        return false;
    }
}
