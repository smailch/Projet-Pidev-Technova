package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AddQuartierController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField nbLampField;

    @FXML
    private TextField consomTotField;

    @FXML
    private ToggleButton themeToggle;

    private QuartierService quartierService = new QuartierService();
    private boolean isDarkMode = false;

    @FXML
    private void handleAddQuartier(ActionEvent event) {
        try {
            // Retrieve data from the form
            String nom = nomField.getText();
            int nbLamp = Integer.parseInt(nbLampField.getText());
            double consomTot = Double.parseDouble(consomTotField.getText());

            // Create a new Quartier object
            Quartier quartier = new Quartier(0, nom, nbLamp, consomTot);

            // Add the Quartier to the database
            quartierService.createQuartier(quartier);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Quartier added successfully!");

            // Clear the form
            clearForm();

            // Navigate to the ListQuartiers view
            loadListQuartiersView(event);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers for 'Number of Lamps' and 'Total Consumption'.");
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while adding the Quartier: " + e.getMessage());
        }
    }

    @FXML
    private void toggleTheme() {
        Scene scene = themeToggle.getScene();
        if (scene != null) {
            if (isDarkMode) {
                scene.getStylesheets().remove(getClass().getResource("/com/example/demo/light-theme.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm());
                themeToggle.setText("Light Mode");
            } else {
                scene.getStylesheets().remove(getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/com/example/demo/light-theme.css").toExternalForm());
                themeToggle.setText("Dark Mode");
            }
            isDarkMode = !isDarkMode;
        }
    }

    @FXML
    private void goToMenu(ActionEvent event) throws IOException {
        // Load the menu scene
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/demo/main.fxml"));
        javafx.scene.Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void loadListQuartiersView(ActionEvent event) throws IOException {
        // Load the list-quartiers.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/list-quartiers.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the new scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nomField.clear();
        nbLampField.clear();
        consomTotField.clear();
    }
}