package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.example.demo.services.QuartierService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UpdateLampadaireDialogController {

    @FXML
    private TextField localisationField;

    @FXML
    private CheckBox etatField;

    @FXML
    private TextField consommationField;

    @FXML
    private ComboBox<Quartier> quartierComboBox;

    @FXML
    private DatePicker dateInstallationField;

    @FXML
    private ToggleButton darkModeToggle;

    @FXML
    private StackPane rootPane;

    private Lampadaire selectedLampadaire;
    private final LampadaireService lampadaireService = new LampadaireService();
    private final QuartierService quartierService = new QuartierService();

    @FXML
    public void initialize() {
        // Apply the same CSS as in AddLampadaireController
        Platform.runLater(() -> {
            if (localisationField.getScene() != null) {
                String css = getClass().getResource("/com/example/demo/modern-purple.css").toExternalForm();
                localisationField.getScene().getStylesheets().clear();
                localisationField.getScene().getStylesheets().add(css);
            }
        });

        // Make localisationField non-editable
        localisationField.setEditable(false);
        localisationField.setDisable(true);

        // Populate the ComboBox with Quartier names
        populateQuartierComboBox();
        setupDarkMode();
    }

    private void populateQuartierComboBox() {
        try {
            List<Quartier> quartiers = quartierService.getAllQuartiers();
            quartierComboBox.getItems().addAll(quartiers);

            // Set a custom cell factory to display only the Quartier name
            quartierComboBox.setCellFactory(lv -> new ListCell<Quartier>() {
                @Override
                protected void updateItem(Quartier quartier, boolean empty) {
                    super.updateItem(quartier, empty);
                    setText(empty ? "" : quartier.getNom()); // Assuming getNom() returns the name of the Quartier
                }
            });

            // Set a custom button cell to display only the Quartier name when an item is selected
            quartierComboBox.setButtonCell(new ListCell<Quartier>() {
                @Override
                protected void updateItem(Quartier quartier, boolean empty) {
                    super.updateItem(quartier, empty);
                    setText(empty ? "" : quartier.getNom()); // Assuming getNom() returns the name of the Quartier
                }
            });
        } catch (SQLException e) {
            System.err.println("Error loading quartiers: " + e.getMessage());
        }
    }

    private void setupDarkMode() {
        Platform.runLater(() -> {
            Scene scene = rootPane.getScene();
            if (scene != null) {
                String lightTheme = getClass().getResource("/com/example/demo/light-theme.css").toExternalForm();
                String darkTheme = getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm();

                // Get current hour
                int currentHour = LocalDate.now().atTime(java.time.LocalTime.now()).getHour();

                if (currentHour >= 18 || currentHour < 6) {
                    // Enable dark mode automatically at night
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(darkTheme);
                    darkModeToggle.setSelected(true);
                } else {
                    // Default to light mode during the day
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(lightTheme);
                    darkModeToggle.setSelected(false);
                }
            }
        });

        // Toggle between light and dark themes
        darkModeToggle.setOnAction(event -> {
            Scene scene = rootPane.getScene();
            if (scene != null) {
                String lightTheme = getClass().getResource("/com/example/demo/light-theme.css").toExternalForm();
                String darkTheme = getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm();

                if (darkModeToggle.isSelected()) {
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(darkTheme);
                } else {
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(lightTheme);
                }
            }
        });
    }

    public void setSelectedLampadaire(Lampadaire lampadaire) {
        this.selectedLampadaire = lampadaire;

        // Populate fields with selected Lampadaire data
        localisationField.setText(lampadaire.getLocalisation());
        etatField.setSelected(lampadaire.isEtat());
        consommationField.setText(String.valueOf(lampadaire.getConsommation()));
        dateInstallationField.setValue(lampadaire.getDate_installation());

        // Set the selected Quartier in the ComboBox
        try {
            Quartier selectedQuartier = quartierService.getQuartierById(lampadaire.getId_quartier());
            quartierComboBox.getSelectionModel().select(selectedQuartier);
        } catch (SQLException e) {
            System.err.println("Error loading selected quartier: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Update Lampadaire with new values
            selectedLampadaire.setEtat(etatField.isSelected());
            selectedLampadaire.setConsommation(Double.parseDouble(consommationField.getText()));
            selectedLampadaire.setDate_installation(dateInstallationField.getValue());

            Quartier selectedQuartier = quartierComboBox.getValue();
            selectedLampadaire.setId_quartier(selectedQuartier != null ? selectedQuartier.getId() : 0);

            // Save updated Lampadaire
            lampadaireService.updateLampadaire(selectedLampadaire);

            // Close dialog
            Stage stage = (Stage) localisationField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error updating Lampadaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Close window without saving
        Stage stage = (Stage) localisationField.getScene().getWindow();
        stage.close();
    }
}