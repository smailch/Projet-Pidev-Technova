package controllers;

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
import javafx.scene.layout.HBox;
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
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Password Reset");
        alert.setHeaderText(null);
        alert.setContentText("If this email exists in our system, a reset link will be sent.");
        alert.showAndWait();
    }

    @FXML
    private void handleReturnToLogin(Event event) {
        // Get the current stage
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Create custom title bar
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);

        // Switch to the login page
        NavigationUtils.switchPage("/Login.fxml", currentStage, titleBar);
    }


}
