package services;

import entities.Utilisateur;

public class SessionManager {
    private int userId;
    private int dossierId;
    private Utilisateur utilisateurConnecte;

    private static SessionManager instance; // Singleton instance

    private SessionManager() {
        this.userId = -1;
        this.dossierId = -1;
        this.utilisateurConnecte = null;
    }

    public static synchronized SessionManager getInstance() { // Thread-safe Singleton
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUserId(int userId) {
        if (userId < 0) {
            System.out.println("Invalid userId: " + userId);
            return;
        }
        System.out.println("Setting userId to: " + userId);
        this.userId = userId;
    }

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
