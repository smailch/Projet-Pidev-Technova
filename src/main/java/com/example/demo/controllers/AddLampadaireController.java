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

public class AddLampadaireController {

    @FXML
    private TextField localisationField;

    @FXML
    private CheckBox etatField;

    @FXML
    private TextField consommationField;

    @FXML
    private ComboBox<Quartier> quartierComboBox; // Use ComboBox instead of TextField

    @FXML
    private DatePicker dateInstallationField;

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

    @FXML
    private void handleSave() {
        try {
            // Retrieve data from the form
            String localisation = localisationField.getText();
            boolean etat = etatField.isSelected();
            double consommation = Double.parseDouble(consommationField.getText());
            Quartier selectedQuartier = quartierComboBox.getValue(); // Get the selected Quartier
            int idQuartier = selectedQuartier != null ? selectedQuartier.getId() : 0; // Get the ID of the selected Quartier
            LocalDate dateInstallation = dateInstallationField.getValue();

            // Create a new Lampadaire object
            Lampadaire lampadaire = new Lampadaire(0, localisation, etat, consommation, idQuartier, dateInstallation);

            // Save the Lampadaire to the database
            lampadaireService.createLampadaire(lampadaire);

            // Close the window
            Stage stage = (Stage) localisationField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saving Lampadaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        // Close the window without saving
        Stage stage = (Stage) localisationField.getScene().getWindow();
        stage.close();
    }
}