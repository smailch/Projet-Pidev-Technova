package org.example.demo1.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.demo1.models.Camion_Collecte;
import org.example.demo1.models.Zone;
import org.example.demo1.services.CamionCollecteService;
import org.example.demo1.services.ZoneService;

public class EditCamionController {

    @FXML
    private TextField capaciteMaxField;

    @FXML
    private ComboBox<String> statutComboBox; // ComboBox for predefined statuts

    @FXML
    private ComboBox<String> zoneComboBox; // ComboBox for zone location names

    private final CamionCollecteService camionService = new CamionCollecteService();
    private final ZoneService zoneService = new ZoneService(); // Add ZoneService
    private Camion_Collecte camion;
    private ObservableList<Zone> zones; // List of zones (to get IDs from names)

    public void initialize() {
        // Populate the statutComboBox with predefined values
        ObservableList<String> statuts = FXCollections.observableArrayList(
                "actif", "not actif", "damaged", "out of service"
        );
        statutComboBox.setItems(statuts);

        // Load zones into the ComboBox
        zones = zoneService.getAllZones();
        ObservableList<String> zoneNames = FXCollections.observableArrayList();
        for (Zone zone : zones) {
            zoneNames.add(zone.getLocation());
        }
        zoneComboBox.setItems(zoneNames);

        // Optional: Set default selections
        if (!zoneNames.isEmpty()) {
            zoneComboBox.getSelectionModel().select(0);
        }
        if (!statuts.isEmpty()) {
            statutComboBox.getSelectionModel().select(0);
        }
    }

    public void setCamion(Camion_Collecte camion) {
        this.camion = camion;
        capaciteMaxField.setText(String.valueOf(camion.getCapacite_max()));

        // Set the current statut
        statutComboBox.setValue(camion.getStatut());

        // Find the zone by ID and set the ComboBox value
        Zone currentZone = zoneService.getZoneById(camion.getZone_id());
        if (currentZone != null) {
            zoneComboBox.setValue(currentZone.getLocation());
        }
    }

    @FXML
    private void handleSave() {
        try {
            double capaciteMax = Double.parseDouble(capaciteMaxField.getText());
            String statut = statutComboBox.getSelectionModel().getSelectedItem();

            // Find the selected zone's ID
            String selectedZoneName = zoneComboBox.getSelectionModel().getSelectedItem();
            int zoneId = -1;
            for (Zone zone : zones) {
                if (zone.getLocation().equals(selectedZoneName)) {
                    zoneId = zone.getId();
                    break;
                }
            }

            if (zoneId == -1) {
                System.err.println("Invalid zone selection.");
                return;
            }

            // Save the camion with the updated values
            boolean success = camionService.modifyCamion(camion.getId(), capaciteMax, statut, zoneId);
            if (success) {
                System.out.println("Camion updated successfully.");
                closeWindow();
            } else {
                System.err.println("Failed to update camion.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) capaciteMaxField.getScene().getWindow();
        stage.close();
    }
}