package controllers;

public class SharedDataController {

    private static SharedDataController instance;
    private String userEmail;

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
}
