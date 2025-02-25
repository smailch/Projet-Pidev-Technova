package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;

public class AddQuartierController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField nbLampField;

    @FXML
    private TextField consomTotField;

    private QuartierService quartierService = new QuartierService();

    @FXML
    private void handleAddQuartier() {
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
            showAlert(AlertType.INFORMATION, "Success", "Quartier added successfully!");

            // Clear the form
            clearForm();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Input Error", "Please enter valid numbers for 'Number of Lamps' and 'Total Consumption'.");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while adding the Quartier: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
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