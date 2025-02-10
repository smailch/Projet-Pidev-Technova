package test;

import entities.Utilisateur;
import entities.Role;
import services.UtilisateurService;
import tools.Myconnection;
import java.util.Calendar;

public class MainClass {
    public static void main(String[] args) {
        Myconnection myconnection = Myconnection.getInstance(); // Connexion à la base de données

        // Obtenir la date actuelle sans l'heure
        Calendar calendar = Calendar.getInstance();
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());

        // Création d'une instance de la classe Utilisateur
        Utilisateur utilisateur = new Utilisateur(11,"mourad ", "Missaoui", "missaoui.mourad@esprit.tn", Role.Admin, sqlDate, "123456");

        // Création d'une instance de UtilisateurService
        UtilisateurService utilisateurService = new UtilisateurService();

        // Ajouter un utilisateur avec la méthode addEntity (utilisant PreparedStatement)
        //utilisateurService.addEntity(utilisateur);

        // Envoyer un email après l'ajout de l'utilisateur
        //utilisateurService.EnvoyerEmail(utilisateur);  // 🔥 Appel de la méthode d'envoi d'email

        // Mettre à jour un utilisateur
        //utilisateur.setNom("NouveauNom");
       // utilisateurService.updateEntity(utilisateur);
        utilisateurService.deleteEntity(utilisateur);

        // UtilisateurService utilisateurService = new UtilisateurService();

        String email = "jihadd.ch@esprit.tn";
        String motDePasse = "123456";

        Utilisateur utilisateurConnecte = utilisateurService.connexion(email, motDePasse);
        if (utilisateurConnecte != null) {
            System.out.println("👤 Utilisateur connecté : " + utilisateurConnecte.getNom());
            utilisateurService.deconnexion(utilisateurConnecte);
        } else {
            System.out.println("❌ Échec de la connexion.");
        }
    }


}


