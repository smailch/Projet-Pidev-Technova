package Test;

import Entities.Utilisateur;
import Entities.Role;
import Services.UtilisateurService;
import Tools.Myconnection;
import java.util.List;
import java.util.Date;

public class MainClass {
    public static void main(String[] args) {
        Myconnection myconnection = Myconnection.getInstance(); // Connexion à la base de données

        // Création d'une instance de la classe Utilisateur
        Utilisateur utilisateur = new Utilisateur( "Smailss", "Chemlali", "smail@exemple.com", Role.Admin, new Date());

        // Création d'une instance de UtilisateurService
        UtilisateurService utilisateurService = new UtilisateurService();

        // Ajouter un utilisateur avec la méthode addEntity2 (utilisant PreparedStatement)
        utilisateurService.addEntity2(utilisateur);

        // Récupérer et afficher tous les utilisateurs
      //  List<Utilisateur> utilisateurs = utilisateurService.getAllData();
        //for (Utilisateur u : utilisateurs) {
       //     System.out.println(u);
        //}

        // Mettre à jour un utilisateur
       // utilisateur.setNom("NouveauNom");
       // utilisateurService.updateEntity(utilisateur);

        // Supprimer un utilisateur
       // utilisateurService.deleteEntity(utilisateur);
    }
}
