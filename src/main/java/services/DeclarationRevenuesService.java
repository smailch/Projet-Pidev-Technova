package services;

import entities.DeclarationRevenues;
import interfaces.IService;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeclarationRevenuesService implements IService<DeclarationRevenues> {

    @Override
    public void addEntity(DeclarationRevenues declarationRevenues) {
        String req = "INSERT INTO declarationrevenus(id,id_dossier, montant_revenu, source_revenu, date_declaration, preuve_revenu) " +
                "VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, declarationRevenues.getId());
            pst.setInt(2, declarationRevenues.getIdDossier());
            pst.setDouble(3, declarationRevenues.getMontantRevenu());
            pst.setString(4, declarationRevenues.getSourceRevenu());
            pst.setString(5, declarationRevenues.getDateDeclaration());
            pst.setString(6, declarationRevenues.getPreuveRevenu());
            pst.executeUpdate();
            System.out.println("Déclaration de Revenus Ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(DeclarationRevenues declarationRevenues) {
        String req = "DELETE FROM declarationrevenus WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, declarationRevenues.getId());
            pst.executeUpdate();
            System.out.println("Déclaration de Revenus Supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteEntityWithID(int id) {
        String req = "DELETE FROM declarationrevenus WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Déclaration de Revenus Supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void updateEntity(DeclarationRevenues declarationRevenues) {
        String req = "UPDATE declarationrevenus SET id_dossier = ?, montant_revenu = ?, source_revenu = ?, " +
                "date_declaration = ?, preuve_revenu = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, declarationRevenues.getIdDossier());
            pst.setDouble(2, declarationRevenues.getMontantRevenu());
            pst.setString(3, declarationRevenues.getSourceRevenu());
            pst.setString(4, declarationRevenues.getDateDeclaration());
            pst.setString(5, declarationRevenues.getPreuveRevenu());
            pst.setInt(6, declarationRevenues.getId());
            pst.executeUpdate();
            System.out.println("Déclaration de Revenus Mise à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public List<DeclarationRevenues> getAllData() {
        List<DeclarationRevenues> result = new ArrayList<>();
        String req = "SELECT * FROM declarationrevenus";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                DeclarationRevenues declaration = new DeclarationRevenues();
                declaration.setId(rs.getInt(1));
                declaration.setIdDossier(rs.getInt(2));
                declaration.setMontantRevenu(rs.getDouble(3));
                declaration.setSourceRevenu(rs.getString(4));
                declaration.setDateDeclaration(rs.getString(5));
                declaration.setPreuveRevenu(rs.getString(6));
                result.add(declaration);
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
