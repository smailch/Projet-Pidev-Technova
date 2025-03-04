package services;

import entities.Utilisateur;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {} // Constructeur privé pour éviter l'instanciation multiple

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }
}