package services;

public class SessionManager {
    private int userId = -1; // Remove static
    private int dossierId = -1;

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
    }

    public void setUserId(int userId) {
        System.out.println("Setting userId to: " + userId);
        this.userId = userId;  // No more static reference
    }

    public int getDossierId() {
        return dossierId;
    }

    public void setDossierId(int dossierId) {
        this.dossierId = dossierId;
    }
}
