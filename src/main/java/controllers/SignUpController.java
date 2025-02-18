package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
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
    private TextField txtPrenom;

    @FXML
    private TextField txtEmail;

    @FXML
    private ComboBox<Role> comboRole;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private Button btnSignUp;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void onSignUpButtonClicked() {
        String errorMessage = null;

        if (txtNom.getText().trim().isEmpty()) {
            errorMessage = "Veuillez entrer un nom.";
        } else if (txtPrenom.getText().trim().isEmpty()) {
            errorMessage = "Veuillez entrer un prénom.";
        } else {
            String email = txtEmail.getText();
            if (!isValidEmail(email)) {
                errorMessage = "Email invalide.";
            } else if (utilisateurService.emailExists(email)) {
                errorMessage = "Cet email est déjà utilisé.";
            } else {
                String password = txtPassword.getText();
                String confirmPassword = txtConfirmPassword.getText();
                if (!password.equals(confirmPassword)) {
                    errorMessage = "Les mots de passe ne correspondent pas.";
                } else if (passwordStrengthBar.getProgress() < 0.5) {
                    errorMessage = "Mot de passe trop faible.";
                }
            }
        }

        if (errorMessage != null) {
            showAlert("Erreur", errorMessage, Alert.AlertType.ERROR);
            return;
        }

        // Création de l'utilisateur
        Calendar calendar = Calendar.getInstance();
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
        Role selectedRole = comboRole.getValue();
        Utilisateur utilisateur = new Utilisateur(txtNom.getText(), txtPrenom.getText(), txtEmail.getText(), selectedRole, sqlDate, txtPassword.getText());
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

        redirectToLogin();
    }

    @FXML
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

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

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #87CEEB;" + // 🌟 Bleu ciel
                        "-fx-border-color: #0073e6;" + // Bordure bleu foncé
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);" // Ombre
        );

        // Appliquer un style au titre
        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle(
                    "-fx-background-color: #0073e6;" + // Bleu profond
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10px;"
            );
        }

        // Appliquer un style au texte du message
        if (dialogPane.lookup(".content.label") != null) {
            dialogPane.lookup(".content.label").setStyle(
                    "-fx-text-fill: #ffffff;" + // Texte blanc
                            "-fx-font-size: 15px;" +
                            "-fx-font-family: 'Arial';" +
                            "-fx-padding: 10px;"
            );
        }

        // Charger une police personnalisée

        alert.showAndWait();
    }

}