package org.example.demo1.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.demo1.models.Camion_Collecte;
import org.example.demo1.models.Zone;
import org.example.demo1.services.CamionCollecteService;
import org.example.demo1.services.ZoneService;

import java.util.HashMap;
import java.util.Map;

public class AddCamionController {

    @FXML
    private TextField capaciteMaxField;

    @FXML
    private ComboBox<String> statutComboBox; // ComboBox for statut

    @FXML
    private ComboBox<String> zoneComboBox; // ComboBox for zone names

    private CamionCollecteService camionCollecteService = new CamionCollecteService();
    private ZoneService zoneService = new ZoneService();

    // A map to store the mapping between zone names and their IDs
    private Map<String, Integer> zoneMap = new HashMap<>();

    // Initialize method to set up the ComboBox options
    @FXML
    private void initialize() {
        // Add predefined options for the statut ComboBox
        statutComboBox.getItems().addAll("actif", "not actif", "damaged", "out of service");
        statutComboBox.setValue("actif"); // Set default value

        // Load zones into the zoneComboBox
        loadZones();
    }

    // Load zones from the database and populate the zoneComboBox
    private void loadZones() {
        ObservableList<Zone> zones = zoneService.getAllZones(); // Fetch all zones
        ObservableList<String> zoneNames = FXCollections.observableArrayList();

        for (Zone zone : zones) {
            zoneNames.add(zone.getLocation()); // Add zone names to the ComboBox
            zoneMap.put(zone.getLocation(), zone.getId()); // Map zone name to its ID
        }

        zoneComboBox.setItems(zoneNames);
        if (!zoneNames.isEmpty()) {
            zoneComboBox.setValue(zoneNames.get(0)); // Set default value
        }
    }

    // Handles the "Add Camion" button click
    @FXML
    private void handleAddCamion() {
        try {
            // Parse input values
            Double capaciteMax = Double.parseDouble(capaciteMaxField.getText());
            String statut = statutComboBox.getValue(); // Get selected value from status ComboBox
            String selectedZoneName = zoneComboBox.getValue(); // Get selected zone name

            // Get the corresponding zone ID from the map
            Integer zoneId = zoneMap.get(selectedZoneName);

            if (zoneId == null) {
                showAlert(AlertType.ERROR, "Error", "Invalid zone selected.");
                return;
            }

            // Create a new Camion_Collecte object
            Camion_Collecte camion = new Camion_Collecte();
            camion.setCapacite_max(capaciteMax);
            camion.setStatut(statut);
            camion.setZone_id(zoneId);

            // Add the camion to the database using the service
            camionCollecteService.addCamion(camion);

            // Show success message
            showAlert(AlertType.INFORMATION, "Success", "Camion added successfully!");

            // Clear the form fields
            capaciteMaxField.clear();
            statutComboBox.setValue("actif"); // Reset ComboBox to default value
            zoneComboBox.setValue(null);
        } catch (NumberFormatException e) {
            // Handle invalid input
            showAlert(AlertType.ERROR, "Error", "Invalid input. Please enter valid values.");
        } catch (Exception e) {
            // Handle any other exceptions
            showAlert(AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    // Utility method to show alerts
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}