package services;
import entities.DossierFiscale;
import interfaces.IService;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DossierFiscaleService implements IService<DossierFiscale> {

    @Override
    public void addEntity(DossierFiscale dossierFiscale) {
            String req = "INSERT INTO DossierFiscale(id, id_user, annee_fiscale, total_impot, total_impot_paye, status, date_creation, moyen_paiement) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, dossierFiscale.getId());
            pst.setInt(2, dossierFiscale.getIdUser());
            pst.setInt(3, dossierFiscale.getAnneeFiscale());
            pst.setDouble(4, dossierFiscale.getTotalImpot());
            pst.setDouble(5, dossierFiscale.getTotalImpotPaye());
            pst.setString(6, dossierFiscale.getStatus());
            pst.setString(7, dossierFiscale.getDateCreation());
            pst.setString(8, dossierFiscale.getMoyenPaiement());
            pst.executeUpdate();
            System.out.println("Dossier Fiscale Ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(DossierFiscale dossierFiscale) {
        String req = "DELETE FROM DossierFiscale WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, dossierFiscale.getId());
            pst.executeUpdate();
            System.out.println("Dossier Fiscale Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteEntityWithID(int id) {
        String req = "DELETE FROM DossierFiscale WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Dossier Fiscale Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(DossierFiscale dossierFiscale) {
        String req = "UPDATE DossierFiscale SET id_user = ?, annee_fiscale = ?, total_impot = ?, total_impot_paye = ?, " +
                "status = ?, date_creation = ?, moyen_paiement = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, dossierFiscale.getIdUser());
            pst.setInt(2, dossierFiscale.getAnneeFiscale());
            pst.setDouble(3, dossierFiscale.getTotalImpot());
            pst.setDouble(4, dossierFiscale.getTotalImpotPaye());
            pst.setString(5, dossierFiscale.getStatus());
            pst.setString(6, dossierFiscale.getDateCreation());
            pst.setString(7, dossierFiscale.getMoyenPaiement());
            pst.setInt(8, dossierFiscale.getId());
            pst.executeUpdate();
            System.out.println("Dossier Fiscale Mis à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<DossierFiscale> getAllData() {
        List<DossierFiscale> result = new ArrayList<>();
        String req = "SELECT * FROM DossierFiscale";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                DossierFiscale dossier = new DossierFiscale();
                dossier.setId(rs.getInt(1));
                dossier.setIdUser(rs.getInt(2));
                dossier.setAnneeFiscale(rs.getInt(3));
                dossier.setTotalImpot(rs.getDouble(4));
                dossier.setTotalImpotPaye(rs.getDouble(5));
                dossier.setStatus(rs.getString(6));
                dossier.setDateCreation(rs.getString(7));
                dossier.setMoyenPaiement(rs.getString(8));
                result.add(dossier);
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

    public double getResteImpot(DossierFiscale dossier) {
        return dossier.getTotalImpot()-dossier.getTotalImpotPaye() ;
    }
    public void ExportPDF(DossierFiscale dossier){
        PDFGenerator.GeneratePDF(dossier);
    }
    public void ExportExcel(String file){
        ExcelGenerator.exportDossiersToExcel(this.getAllData(),file);
    }
}
