package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateQuartierController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField nbLampField;

    @FXML
    private TextField consomTotField;

    private Quartier selectedQuartier;
    private QuartierService quartierService = new QuartierService();
    private Stage stage;

    public void setSelectedQuartier(Quartier selectedQuartier) {
        this.selectedQuartier = selectedQuartier;
        // Populate fields with selected Quartier data
        nomField.setText(selectedQuartier.getNom());
        nbLampField.setText(String.valueOf(selectedQuartier.getNbLamp()));
        consomTotField.setText(String.valueOf(selectedQuartier.getConsomTot()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        try {
            // Update the selected Quartier with new values
            selectedQuartier.setNom(nomField.getText());
            selectedQuartier.setNbLamp(Integer.parseInt(nbLampField.getText()));
            selectedQuartier.setConsomTot(Double.parseDouble(consomTotField.getText()));

            // Save the updated Quartier to the database
            quartierService.updateQuartier(selectedQuartier);

            // Close the pop-up window
            stage.close();
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.err.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            // Handle other errors
            System.err.println("Error updating Quartier: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Close the pop-up window without saving
        stage.close();
    }
}