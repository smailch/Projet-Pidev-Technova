package com.example.demo;

import com.example.demo.entities.Role;
import com.example.demo.entities.Utilisateur;
import com.example.demo.services.UtilisateurService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;

    @FXML
    private TextField txtEmail;

    @FXML
    private ComboBox<String> comboRole;

    @FXML
    private DatePicker dateInscription;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ProgressBar passwordStrengthBar;

    @FXML
    private Button btnSignUp;

    @FXML
    private Label lblErrors;

    // Expression régulière pour valider l'email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Méthode qui s'exécute lors du clic sur le bouton "S'inscrire"

    @FXML
    public void onSignUpButtonClicked() {
        // Vérification des champs
        String email = txtEmail.getText();
        if (!isValidEmail(email)) {
            lblErrors.setText("Email invalide. Veuillez entrer un email valide.");
            return;
        }

        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        if (!password.equals(confirmPassword)) {
            lblErrors.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        if (passwordStrengthBar.getProgress() < 0.5) {
            lblErrors.setText("Le mot de passe est trop faible. Veuillez choisir un mot de passe plus fort.");
            return;
        }

        // Si tout est valide, procéder à l'inscription
        lblErrors.setText(""); // Effacer les erreurs

        // Créer l'utilisateur à partir des champs du formulaire
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(txtNom.getText());
        utilisateur.setPrenom(txtPrenom.getText());
        utilisateur.setEmail(txtEmail.getText());
        utilisateur.setRole(Role.valueOf(comboRole.getValue()));  // Assuming comboRole contains a role selection
        utilisateur.setDateInscription(java.sql.Date.valueOf(dateInscription.getValue())); // Date format from DatePicker
        utilisateur.setMotDePasse(txtPassword.getText());

        // Appeler la méthode pour ajouter l'utilisateur
        UtilisateurService utilisateurService = new UtilisateurService();
        utilisateurService.addEntity(utilisateur); // Add the user to the database

        // Afficher un message de succès
        lblErrors.setText("Utilisateur inscrit avec succès!");

        // Redirection vers la page d'accueil
        try {
            // Fermer la fenêtre actuelle


            // Charger la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/incidentForm.fxml"));
            Scene scene = new Scene(loader.load());

        } catch (IOException ex) {
            // En cas d'erreur lors du chargement de la scène
            System.err.println("Error loading the home page: " + ex.getMessage());
        }
    }



    // Fonction de validation de l'email
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    // Suivi de la force du mot de passe
    @FXML
    public void initialize() {
        txtPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updatePasswordStrength(newValue);
            }
        });
    }

    // Mise à jour de la barre de force du mot de passe
    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);

        // Changer la couleur de la barre en fonction de la force
        String color;
        if (strength < 0.3) {
            color = "red";  // Faible
        } else if (strength < 0.7) {
            color = "orange";  // Moyen
        } else {
            color = "green";  // Fort
        }
        passwordStrengthBar.setStyle("-fx-accent: " + color + ";");
    }

    // Calcul de la force du mot de passe
    private double calculatePasswordStrength(String password) {
        int length = password.length();
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()-+=].*");

        int score = 0;
        if (length >= 8) score++;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        return score / 5.0; // Normaliser sur une échelle de 0 à 1
    }
    @FXML
    public void onEmailKeyReleased() {
        String email = txtEmail.getText();
        if (isValidEmail(email)) {
            lblErrors.setText(""); // Effacer le message d'erreur si l'email est valide
        } else {
            lblErrors.setText("Email invalide. Veuillez entrer un email valide.");
        }
    }




}
