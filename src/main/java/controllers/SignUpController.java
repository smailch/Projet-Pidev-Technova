package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UtilisateurService;
import entities.Utilisateur;
import entities.Role;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

public class SignUpController {

    @FXML
    private TextField txtNom;
    @FXML
    private Label lblErrorNom;

    @FXML
    private TextField txtPrenom;
    @FXML
    private Label lblErrorPrenom;

    @FXML
    private TextField txtEmail;
    @FXML
    private Label lblErrorEmail;

    @FXML
    private ComboBox<Role> comboRole;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblErrorPassword;

    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblErrorConfirmPassword;

    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private Button btnSignUp;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void onSignUpButtonClicked() {
        boolean isValid = true;

        // Vérification des champs
        if (txtNom.getText().trim().isEmpty()) {
            lblErrorNom.setText("Veuillez entrer un nom.");
            isValid = false;
        } else {
            lblErrorNom.setText("");
        }

        if (txtPrenom.getText().trim().isEmpty()) {
            lblErrorPrenom.setText("Veuillez entrer un prénom.");
            isValid = false;
        } else {
            lblErrorPrenom.setText("");
        }

        String email = txtEmail.getText();
        if (!isValidEmail(email)) {
            lblErrorEmail.setText("Email invalide.");
            isValid = false;
        } else if (utilisateurService.emailExists(email)) {
            lblErrorEmail.setText("Cet email est déjà utilisé.");
            isValid = false;
        } else {
            lblErrorEmail.setText("");
        }

        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        if (!password.equals(confirmPassword)) {
            lblErrorConfirmPassword.setText("Les mots de passe ne correspondent pas.");
            isValid = false;
        } else {
            lblErrorConfirmPassword.setText("");
        }

        if (passwordStrengthBar.getProgress() < 0.5) {
            lblErrorPassword.setText("Mot de passe trop faible.");
            isValid = false;
        } else {
            lblErrorPassword.setText("");
        }

        if (!isValid) return;

        // Création de l'utilisateur
        Calendar calendar = Calendar.getInstance();
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
        Role selectedRole = comboRole.getValue();
        Utilisateur utilisateur = new Utilisateur(txtNom.getText(), txtPrenom.getText(), email, selectedRole, sqlDate, password);
        utilisateurService.addEntity(utilisateur);


        // Affichage de succès et redirection
        showAlertAndRedirect("Succès", "Compte créé avec succès !", Alert.AlertType.INFORMATION);
    }

    private void showAlertAndRedirect(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // Redirection vers la page de connexion
        redirectToLogin();
    }
    @FXML
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml")); // Assure-toi que le chemin est correct
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle (inscription)
            Stage currentStage = (Stage) btnSignUp.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion.", Alert.AlertType.ERROR);
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    @FXML
    public void initialize() {
        comboRole.getItems().setAll(Role.values());

        txtPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updatePasswordStrength(newValue);
            }
        });
    }

    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);
        String color = strength < 0.3 ? "red" : strength < 0.7 ? "orange" : "green";
        passwordStrengthBar.setStyle("-fx-accent: " + color + ";");
    }

    private double calculatePasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (!password.equals(password.toLowerCase())) score++;
        if (!password.equals(password.toUpperCase())) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()-+=].*")) score++;
        return score / 5.0;
    }

    @FXML
    public void onEmailKeyReleased() {
        String email = txtEmail.getText();
        if (!isValidEmail(email)) {
            lblErrorEmail.setText("Email invalide.");
        } else if (utilisateurService.emailExists(email)) {
            lblErrorEmail.setText("Cet email est déjà utilisé.");
        } else {
            lblErrorEmail.setText("");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
