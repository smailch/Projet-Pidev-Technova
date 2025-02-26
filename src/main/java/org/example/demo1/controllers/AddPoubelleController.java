package org.example.demo1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.demo1.models.Poubelle_Intelligente;
import org.example.demo1.models.Zone;
import org.example.demo1.services.PoubelleIntelligenteService;
import org.example.demo1.services.ZoneService;

import java.util.List;

public class AddPoubelleController {

    @FXML
    private TextField typeDechetsField;

    @FXML
    private TextField niveauRemplissageField;

    @FXML
    private TextField localisationField;

    @FXML
    private ComboBox<Zone> zoneComboBox; // ComboBox for zone selection

    private final PoubelleIntelligenteService poubelleService = new PoubelleIntelligenteService();
    private final ZoneService zoneService = new ZoneService();

    @FXML
    public void initialize() {
        // Populate the ComboBox with zones when the controller is initialized
        List<Zone> zones = zoneService.getAllZones();
        zoneComboBox.getItems().addAll(zones);

        // Set up a StringConverter to display the zone's location in the ComboBox
        zoneComboBox.setConverter(new StringConverter<Zone>() {
            @Override
            public String toString(Zone zone) {
                if (zone != null) {
                    return zone.getLocation(); // Display the location of the zone
                } else {
                    return "";
                }
            }

            @Override
            public Zone fromString(String string) {
                // This method is not needed for display purposes, but it's required by the interface
                return null;
            }
        });
    }

    @FXML
    public void handleAddPoubelle() {
        try {
            // Retrieve values from the form
            String typeDechets = typeDechetsField.getText();
            double niveauRemplissage = Double.parseDouble(niveauRemplissageField.getText());
            String localisation = localisationField.getText();
            Zone selectedZone = zoneComboBox.getSelectionModel().getSelectedItem();

            if (selectedZone == null) {
                System.err.println("Please select a zone.");
                return;
            }

            // Create a new Poubelle_Intelligente object
            Poubelle_Intelligente poubelle = new Poubelle_Intelligente();
            poubelle.setType_dechets(typeDechets);
            poubelle.setNiveau_remplissage(niveauRemplissage);
            poubelle.setLocalisation(localisation);
            poubelle.setZoneId(selectedZone.getId()); // Set the selected zone's ID

            // Add the poubelle to the database
            poubelleService.addPoubelle(poubelle);

            // Clear the form after successful addition
            typeDechetsField.clear();
            niveauRemplissageField.clear();
            localisationField.clear();
            zoneComboBox.getSelectionModel().clearSelection();

            System.out.println("Poubelle added successfully!");
        } catch (NumberFormatException e) {
            System.err.println("Invalid input for niveau_remplissage. Please enter a valid number.");
        } catch (Exception e) {
            System.err.println("Error adding poubelle: " + e.getMessage());
        }
    }
}