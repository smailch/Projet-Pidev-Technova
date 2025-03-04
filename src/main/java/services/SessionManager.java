package services;

import entities.Utilisateur;

public class SessionManager {
    private int userId = -1; // Remove static
    private int dossierId = -1;
    private Utilisateur utilisateurConnecte;

    private static SessionManager instance; // Singleton instance

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUserId(int userId) {
        System.out.println("Setting userId to: " + userId);
        this.userId = userId;  // No more static reference
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public int getDossierId() {
        return dossierId;
    }

    public void setDossierId(int dossierId) {
        this.dossierId = dossierId;
    }
}

