package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.UtilisateurService;
import tools.Myconnection;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;

import static services.UtilisateurService.hasherMotDePasse;

public class ResetPasswordController {

    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private TextField txtEmail;  // Nouveau champ pour l'email

    // Méthode pour calculer la force du mot de passe
    @FXML
    private void updatePasswordStrength() {
        String password = txtNewPassword.getText();
        double strength = calculatePasswordStrength(password);

        // Mise à jour de la barre de progression en fonction de la force du mot de passe
        passwordStrengthBar.setProgress(strength);
    }

    // Méthode pour calculer la force du mot de passe (exemple simple)
    private double calculatePasswordStrength(String password) {
        int score = 0;

        // Logique simple pour estimer la force du mot de passe
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;

        // Retourner la force entre 0 et 1
        return score / 4.0;
    }

    // Méthode pour gérer la réinitialisation du mot de passe
    @FXML
    private void handleResetPassword() {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String email = SharedDataController.getInstance().getUserEmail();

        // Vérification que les mots de passe correspondent
        if (newPassword.equals(confirmPassword)) {
            // Appeler la fonction pour mettre à jour le mot de passe en base de données
            updatePasswordByEmail(email, newPassword);
            // REDERECTION VER LOGIN
            redirectToLogin();

        } else {
            // Afficher un message d'erreur
            showAlert(AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
        }
    }

    // Fonction pour mettre à jour le mot de passe dans la base de données par email
    private void updatePasswordByEmail(String email, String newPassword) {
        UtilisateurService utilisateurService = new UtilisateurService();

        if (!utilisateurService.isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Erreur", "L'email n'est pas valide !");
            return;
        }

        try {
            // Hacher le mot de passe avant de l'enregistrer
            String hashedPassword = hasherMotDePasse(newPassword);

            // Requête SQL pour mettre à jour le mot de passe
            String req = "UPDATE `utilisateur` SET `motDePasse`='" + hashedPassword + "' WHERE `email` = '" + email + "'";

            // Création de la requête et exécution
            Statement st = Myconnection.getInstance().getCnx().createStatement();
            int rowsUpdated = st.executeUpdate(req); // Exécution de la mise à jour

            if (rowsUpdated > 0) {
                showAlert(AlertType.INFORMATION, "Succès", "Le mot de passe a été mis à jour avec succès !");
                System.out.println(newPassword);
            } else {
                showAlert(AlertType.WARNING, "Avertissement", "Aucun utilisateur trouvé avec cet email.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du mot de passe: " + e.getMessage());
        }
    }



    // Fonction pour afficher une alerte avec un type, titre et message
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas d'en-tête pour l'alerte
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml")); // Assure-toi que le chemin est correct
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle (inscription)
            Stage currentStage = (Stage) txtNewPassword.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
