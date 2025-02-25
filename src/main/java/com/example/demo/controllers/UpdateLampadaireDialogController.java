package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.example.demo.services.QuartierService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UpdateLampadaireDialogController {

    @FXML
    private TextField localisationField;

    @FXML
    private CheckBox etatField; // Changed from TextField to CheckBox

    @FXML
    private TextField consommationField;

    @FXML
    private ComboBox<Quartier> quartierComboBox; // Changed from TextField to ComboBox

    @FXML
    private DatePicker dateInstallationField;

    private Lampadaire selectedLampadaire;
    private LampadaireService lampadaireService = new LampadaireService();
    private QuartierService quartierService = new QuartierService();

    @FXML
    public void initialize() {
        // Populate the ComboBox with quartier names
        try {
            List<Quartier> quartiers = quartierService.getAllQuartiers();
            quartierComboBox.getItems().addAll(quartiers);
            quartierComboBox.setCellFactory(lv -> new ListCell<Quartier>() {
                @Override
                protected void updateItem(Quartier quartier, boolean empty) {
                    super.updateItem(quartier, empty);
                    setText(empty ? "" : quartier.getNom());
                }
            });
            quartierComboBox.setButtonCell(new ListCell<Quartier>() {
                @Override
                protected void updateItem(Quartier quartier, boolean empty) {
                    super.updateItem(quartier, empty);
                    setText(empty ? "" : quartier.getNom());
                }
            });
        } catch (SQLException e) {
            System.err.println("Error loading quartiers: " + e.getMessage());
        }
    }

    public void setSelectedLampadaire(Lampadaire lampadaire) {
        this.selectedLampadaire = lampadaire;
        // Populate fields with selected Lampadaire data
        localisationField.setText(lampadaire.getLocalisation());
        etatField.setSelected(lampadaire.isEtat()); // Set CheckBox state
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
            // Update the selected Lampadaire with new values
            selectedLampadaire.setLocalisation(localisationField.getText());
            selectedLampadaire.setEtat(etatField.isSelected()); // Get CheckBox state
            selectedLampadaire.setConsommation(Double.parseDouble(consommationField.getText()));
            Quartier selectedQuartier = quartierComboBox.getValue(); // Get the selected Quartier
            selectedLampadaire.setId_quartier(selectedQuartier != null ? selectedQuartier.getId() : 0); // Set the ID of the selected Quartier
            selectedLampadaire.setDate_installation(dateInstallationField.getValue());

            // Save the updated Lampadaire to the database
            lampadaireService.updateLampadaire(selectedLampadaire);

            // Close the dialog
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
        // Close the window without saving
        Stage stage = (Stage) localisationField.getScene().getWindow();
        stage.close();
    }
}