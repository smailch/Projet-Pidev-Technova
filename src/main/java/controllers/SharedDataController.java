package controllers;

import entities.Utilisateur;

public class SharedDataController {

    public static SharedDataController getInstance;
    private static SharedDataController instance;
    private String userEmail;
    private Utilisateur utilisateurConnecte; // Ajouter un champ utilisateur


    private SharedDataController() {}

    public static SharedDataController getInstance() {
        if (instance == null) {
            instance = new SharedDataController();
        }
        return instance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

}

