package controllers;
import javafx.geometry.Insets;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.UtilisateurService;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.util.List;
import java.util.Random;

import static services.UtilisateurService.isValidEmail;
import controllers.*;

public class ForgetPasswordController {

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnResetPassword;

    @FXML
    private Button btnReturnToLogin;

    @FXML
    private Label lblErrors;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // Simuler une base de données d'emails enregistrés et vérifiés
    private List<String> registeredEmails = Arrays.asList("test@example.com", "user@domain.com");
    private List<String> verifiedEmails = Arrays.asList("test@example.com"); // Seuls certains emails sont vérifiés

    @FXML
    private <andom> void handleResetPassword(Event event) {
        String email = txtEmail.getText();

        if (email.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please enter your email.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(AlertType.ERROR, "Error", "Invalid email format.");
            return;
        }

        if (!utilisateurService.emailExists(email)) {
            showAlert(AlertType.ERROR, "Error", "This email does not exist in our system.");
            return;
        }

        // Logique de réinitialisation du mot de passe
        andom random = (andom) new Random();
        int resetCode = 100000 + ((Random) random).nextInt(900000);
        envoyerEmailReset( email, String.valueOf(resetCode));

        showPopupWindow(resetCode);

    }
    public static void envoyerEmailReset(String email, String resetCode) {
        String from = "chemlaliismail388@gmail.com"; // Votre e-mail
        final String username = "chemlaliismail388@gmail.com"; // Votre e-mail
        final String password = "copj rnsn hcix utzr"; // Mot de passe d'application Gmail

        String host = "smtp.gmail.com"; // Serveur SMTP Gmail

        // Chemin du logo (⚠️ Vérifiez que l'image existe)
        String imagePath = "C:\\Users\\ichaa\\Downloads\\dossier\\Projet-Pidev-Technova-Impot\\src\\main\\resources\\assets\\images\\logo.png";

        // Configuration SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Création de la session SMTP
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Lecture du logo en Base64 pour intégration dans l'email
            String base64Image = "";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
                base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            } else {
                System.out.println("❌ Erreur : L'image du logo n'existe pas à l'emplacement : " + imagePath);
                return;
            }

            // 🌟 Contenu HTML de l'email
            String emailContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
                    + "<div style='text-align: center;'>"
                    + "<img src='" + base64Image + "' alt='CiviSmart Logo' style='width: 150px; margin-bottom: 20px;'>"
                    + "</div>"
                    + "<h2 style='color: #2d89ef; text-align: center;'>Réinitialisation de votre mot de passe</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Bonjour,</p>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "Nous avons reçu une demande de réinitialisation de votre mot de passe. Utilisez le code ci-dessous pour réinitialiser votre mot de passe :"
                    + "</p>"
                    + "<div style='text-align: center; margin: 20px;'>"
                    + "<h3 style='background-color: #2d89ef; color: #fff; padding: 10px; border-radius: 5px; display: inline-block;'>" + resetCode + "</h3>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #555;'>"
                    + "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email."
                    + "</p>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>"
                    + "Si vous avez des questions, n'hésitez pas à nous contacter."
                    + "<br>&copy; 2025 CiviSmart - Tous droits réservés.</p>"
                    + "</div>";

            // Création du message principal
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Réinitialisation de votre mot de passe");

            // Ajout du contenu HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContent, "text/html; charset=utf-8");

            // Création du multipart contenant le HTML
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);

            // Ajout du contenu au message
            message.setContent(multipart);

            // Envoi du message
            Transport.send(message);
            System.out.println("📧 Email de réinitialisation envoyé avec succès à " + email);

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    private void showPopupWindow(int resetCode) {
        // Créer un nouveau stage (fenêtre modale)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre principale

        // Label pour le titre
        Label titleLabel = new Label("Enter Reset Code");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Champ de texte pour entrer le code
        TextField textField = new TextField();
        textField.setPromptText("Enter reset code");
        textField.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-pref-width: 220px; " +
                        "-fx-padding: 8px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-border-color: #bdc3c7; " +
                        "-fx-background-radius: 5px;"
        );

        // Bouton de soumission stylisé
        Button submitButton = new Button("Submit");
        submitButton.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand;"
        );
        submitButton.setOnMouseEntered(e -> submitButton.setStyle(
                "-fx-background-color: #2980b9; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand;"
        ));
        submitButton.setOnMouseExited(e -> submitButton.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand;"
        ));

        submitButton.setOnAction(e -> {
            String code = textField.getText();
            verifyResetCode(code, resetCode);  // Vérification du code
            popupStage.close();  // Fermer la fenêtre après soumission
        });

        // Conteneur principal
        VBox vbox = new VBox(15, titleLabel, textField, submitButton);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle(
                "-fx-background-color: #ecf0f1; " +
                        "-fx-border-color: #bdc3c7; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );

        // Création de la scène
        Scene scene = new Scene(vbox, 320, 200);
        popupStage.setScene(scene);
        popupStage.setTitle("Reset Code");
        popupStage.show();
    }

    private void verifyResetCode(String enteredCode, int resetCode) {
        try {
            int enteredCodeInt = Integer.parseInt(enteredCode);  // Convertir le code saisi en entier

            if (enteredCodeInt == resetCode) {
                showAlert(AlertType.INFORMATION, "Success", "Code verified successfully! You can now reset your password.");

                // Enregistrer l'email dans SharedDataController
                String email = txtEmail.getText();
                SharedDataController.getInstance().setUserEmail(email);

                // Rediriger vers ResetPassword.fxml
                Stage stage = (Stage) btnResetPassword.getScene().getWindow();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                    Parent root = loader.load();
                    Scene resetPasswordScene = new Scene(root);
                    stage.setScene(resetPasswordScene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                showAlert(AlertType.ERROR, "Error", "Invalid code. Please try again.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Invalid code format. Please enter a valid 6-digit code.");
        }
    }




    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturnToLogin(Event event) {
        Stage stage = (Stage) btnReturnToLogin.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Scene loginScene = new Scene(root);
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
