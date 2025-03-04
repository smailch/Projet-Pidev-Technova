package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class UpdateQuartierController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField nbLampField;

    @FXML
    private TextField consomTotField;

    @FXML
    private ToggleButton themeToggle;
    private boolean isDarkMode = false;

    private Quartier selectedQuartier;
    private QuartierService quartierService = new QuartierService();
    private Stage stage;



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
    public void initialize() {
        Platform.runLater(() -> {
            Scene scene = themeToggle.getScene();
            if (scene != null) {
                String lightTheme = getClass().getResource("/com/example/demo/light-theme.css").toExternalForm();
                String darkTheme = getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm();

                // Get current hour
                int currentHour = java.time.LocalTime.now().getHour();

                if (currentHour >= 18 || currentHour < 6) {
                    // Enable dark mode automatically at night
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(darkTheme);
                    themeToggle.setSelected(true);
                    themeToggle.setText("Light Mode");
                    isDarkMode = true;
                } else {
                    // Default to light mode during the day
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(lightTheme);
                    themeToggle.setSelected(false);
                    themeToggle.setText("Dark Mode");
                    isDarkMode = false;
                }
            }
        });

        // Keep manual toggle functionality
        themeToggle.setOnAction(event -> toggleTheme());
    }


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