package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.example.demo.services.QuartierService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    private ComboBox<Quartier> quartierComboBox;
    @FXML
    private DatePicker dateInstallationField;

    private LampadaireService lampadaireService = new LampadaireService();
    private QuartierService quartierService = new QuartierService();
    private Lampadaire currentLampadaire;  // For edit mode

    @FXML
    public void initialize() {
        populateQuartierComboBox();
    }

    // Initialize with existing lampadaire data for editing
    public void setLampadaire(Lampadaire lampadaire) {
        this.currentLampadaire = lampadaire;
        populateFields();
    }

    private void populateQuartierComboBox() {
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
            showAlert("Error Loading Quartiers", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateFields() {
        if (currentLampadaire != null) {
            localisationField.setText(currentLampadaire.getLocalisation());
            etatField.setSelected(currentLampadaire.isEtat());
            consommationField.setText(String.valueOf(currentLampadaire.getConsommation()));
            dateInstallationField.setValue(currentLampadaire.getDate_installation());

            // Set quartier selection
            quartierComboBox.getItems().stream()
                    .filter(q -> q.getId() == currentLampadaire.getId_quartier())
                    .findFirst()
                    .ifPresent(quartierComboBox::setValue);
        }
    }

    public void setLocation(double lat, double lng) {
        localisationField.setText(lat + ", " + lng);
    }

    @FXML
    private void openMapWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/map-window.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            MapWindowController controller = loader.getController();
            controller.setMainController(this);
            stage.setTitle("Select Location");
            stage.show();
        } catch (Exception e) {
            showAlert("Map Error", "Cannot open map window: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (validateFields()) {
                Lampadaire lampadaire = createLampadaireFromFields();

                if (currentLampadaire == null) {
                    lampadaireService.createLampadaire(lampadaire);
                } else {
                    lampadaire.setId(currentLampadaire.getId());
                    lampadaireService.updateLampadaire(lampadaire);
                }

                closeWindow();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for consumption", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Save Error", "Error saving lampadaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        if (localisationField.getText().isEmpty() ||
                consommationField.getText().isEmpty() ||
                quartierComboBox.getValue() == null ||
                dateInstallationField.getValue() == null) {

            showAlert("Validation Error", "Please fill all required fields", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private Lampadaire createLampadaireFromFields() {
        return new Lampadaire(
                0, // ID will be set for updates
                localisationField.getText(),
                etatField.isSelected(),
                Double.parseDouble(consommationField.getText()),
                quartierComboBox.getValue().getId(),
                dateInstallationField.getValue()
        );
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) localisationField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}