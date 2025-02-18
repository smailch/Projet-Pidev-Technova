package services;

public class SessionManager {
    private static int userId;

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        SessionManager.userId = userId;
    }
}
