package com.example.demo;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ForgetPasswordController {

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnResetPassword;

    @FXML
    private Button btnReturnToLogin;

    @FXML
    private Label lblErrors;

    // Handle Reset Password Logic
    @FXML
    private void handleResetPassword(ActionEvent event) {
        String email = txtEmail.getText();
        if (email.isEmpty()) {
            lblErrors.setText("Please enter your email.");
            return;
        }

        // Implement the password reset logic here
        // For example, send a reset email or validate the email.
        // Show success or error messages accordingly.

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Password Reset");
        alert.setHeaderText(null);
        alert.setContentText("If this email exists in our system, a reset link will be sent.");
        alert.showAndWait();
    }

    // Handle Return to Login
    @FXML
    private void handleReturnToLogin(Event event) {
        // Get the current stage
        Stage stage = (Stage) btnReturnToLogin.getScene().getWindow();

        // Load the Login page FXML
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();  // Load the FXML file into a Parent object

            // Set the scene with the loaded root node
            Scene loginScene = new Scene(root);
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
