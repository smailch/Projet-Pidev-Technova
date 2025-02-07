package Services;

import Entities.Role;
import Entities.Utilisateur;
import Interfaces.IService;
import Tools.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    @Override
    public void addEntity(Utilisateur utilisateur) {
        try {
            String req = "INSERT INTO `utilisateur`(`id`, `Nom`, `Prenom`, `Email`, `Role`, `DateInscription`) " +
                    "VALUES ('" + utilisateur.getId() + "','" + utilisateur.getNom() + "','" + utilisateur.getPrenom() +
                    "','" + utilisateur.getEmail() + "','" + utilisateur.getRole().name() + "','" + utilisateur.getDateInscription() + "')";

            Statement st = Myconnection.getInstance().getCnx().createStatement();
            st.executeUpdate(req);
            System.out.println("Utilisateur ajouté");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addEntity2(Utilisateur utilisateur) {
        UtilisateurService utilisateurService = new UtilisateurService();
        if (utilisateurService.emailExists(utilisateur.getEmail())) {
            System.out.println("L'email existe déjà !");
        } else {


        try {
            String req = "INSERT INTO `utilisateur`(`id`, `Nom`, `Prenom`, `Email`, `Role`, `DateInscription`) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = Myconnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, utilisateur.getId());
            pst.setString(2, utilisateur.getNom());
            pst.setString(3, utilisateur.getPrenom());
            pst.setString(4, utilisateur.getEmail());
            pst.setString(5, utilisateur.getRole().name());
            pst.setDate(6, new java.sql.Date(utilisateur.getDateInscription().getTime()));
            pst.executeUpdate();
            System.out.println("Utilisateur ajouté");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        }
    }

    @Override
    public void deleteEntity(Utilisateur utilisateur) {
        try {
            String req = "DELETE FROM `utilisateur` WHERE id = " + utilisateur.getId();
            Statement st = Myconnection.getInstance().getCnx().createStatement();
            st.executeUpdate(req);
            System.out.println("Utilisateur supprimé");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Utilisateur utilisateur) {
        UtilisateurService utilisateurService = new UtilisateurService();
        if (utilisateurService.emailExists(utilisateur.getEmail())) {
            System.out.println("L'email existe déjà !");
        } else {


            try {
                String req = "UPDATE `utilisateur` SET `Nom`='" + utilisateur.getNom() +
                        "', `Prenom`='" + utilisateur.getPrenom() +
                        "', `Email`='" + utilisateur.getEmail() +
                        "', `Role`='" + utilisateur.getRole().name() +
                        "', `DateInscription`='" + utilisateur.getDateInscription() +
                        "' WHERE id = " + utilisateur.getId();
                Statement st = Myconnection.getInstance().getCnx().createStatement();
                st.executeUpdate(req);
                System.out.println("Utilisateur mis à jour");
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    @Override
    public List<Utilisateur> getAllData() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            String req = "SELECT * FROM `utilisateur`";
            Statement st = Myconnection.getInstance().getCnx().createStatement();
            ResultSet res = st.executeQuery(req);
            while (res.next()) { //ResultSet /colonnes/table
                Utilisateur u = new Utilisateur();
                u.setId(res.getInt("id"));
                u.setNom(res.getString("Nom"));
                u.setPrenom(res.getString("Prenom"));
                u.setEmail(res.getString("Email"));
                u.setRole(Role.valueOf(res.getString("Role")));
                u.setDateInscription(res.getDate("DateInscription"));
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return utilisateurs;
    }
    public boolean emailExists(String email) {
        try {
            String req = "SELECT COUNT(*) FROM utilisateur WHERE Email = ?";
            PreparedStatement pst = Myconnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, email);
            ResultSet res = pst.executeQuery();
            if (res.next()) {//ResultSet /colonnes/table
                return res.getInt(1) > 0; //ResultSet /ligne/table
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

}
