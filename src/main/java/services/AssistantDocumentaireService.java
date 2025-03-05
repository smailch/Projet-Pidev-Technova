package services;

import entities.AssistantDocumentaire;
import interfaces.IService;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssistantDocumentaireService implements IService<AssistantDocumentaire> {

    @Override
    public void addEntity(AssistantDocumentaire assistant) {
        String req = "INSERT INTO assistantdocumentaire (id, id_utilisateur, id_document, type_assistance, date_demande, status, remarque, rappel_automatique) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, assistant.getId());
            pst.setInt(2, assistant.getIdUtilisateur());
            pst.setInt(3, assistant.getIdDocument());
            pst.setString(4, assistant.getTypeAssistance());
            pst.setString(5, assistant.getDateDemande());
            pst.setString(6, assistant.getStatus());
            pst.setString(7, assistant.getRemarque());
            pst.setBoolean(8, assistant.isRappelAutomatique());
            pst.executeUpdate();
            System.out.println("Assistant Documentaire Ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntity(AssistantDocumentaire assistant) {
        deleteEntityWithID(assistant.getId());
    }

    public void deleteEntityWithID(int id) {
        String req = "DELETE FROM assistantdocumentaire WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Assistant Documentaire Supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntity(AssistantDocumentaire assistant) {
        String req = "UPDATE assistantdocumentaire SET id_utilisateur = ?, id_document = ?, type_assistance = ?, date_demande = ?, status = ?, remarque = ?, rappel_automatique = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, assistant.getIdUtilisateur());
            pst.setInt(2, assistant.getIdDocument());
            pst.setString(3, assistant.getTypeAssistance());
            pst.setTimestamp(4, Timestamp.valueOf(assistant.getDateDemande()));
            pst.setString(5, assistant.getStatus());
            pst.setString(6, assistant.getRemarque());
            pst.setBoolean(7, assistant.isRappelAutomatique());
            pst.setInt(8, assistant.getId());
            pst.executeUpdate();
            System.out.println("Assistant Documentaire Mis à Jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<AssistantDocumentaire> getAllData() {
        List<AssistantDocumentaire> result = new ArrayList<>();
        String req = "SELECT * FROM assistantdocumentaire";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                AssistantDocumentaire assistant = new AssistantDocumentaire();
                assistant.setId(rs.getInt("id"));
                assistant.setIdUtilisateur(rs.getInt("id_utilisateur"));
                assistant.setIdDocument(rs.getInt("id_document"));
                assistant.setTypeAssistance(rs.getString("type_assistance"));
                assistant.setDateDemande(rs.getTimestamp("date_demande").toLocalDateTime().toString());
                assistant.setStatus(rs.getString("status"));
                assistant.setRemarque(rs.getString("remarque"));
                assistant.setRappelAutomatique(rs.getBoolean("rappel_automatique"));
                result.add(assistant);
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