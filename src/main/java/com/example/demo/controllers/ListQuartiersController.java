package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import com.example.demo.utils.PDFGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ListQuartiersController {

    @FXML
    private TableView<Quartier> quartierTable;

    @FXML
    private Slider minRangeSlider;

    @FXML
    private Slider maxRangeSlider;

    @FXML
    private Label minRangeLabel;

    @FXML
    private Label maxRangeLabel;

    @FXML
    private TextField searchField;

    // Initialize range values
    private double minValue = 0;
    private double maxValue = 1000;

    private QuartierService quartierService = new QuartierService();

    @FXML
    public void initialize() {
        // Load data when the view is initialized
        handleRefresh();

        // Add a listener to the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch(newValue);
        });

        // Add a listener to the min range slider to update the label dynamically
        minRangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            minValue = newValue.doubleValue();
            minRangeLabel.setText(String.format("Min: %.0f", minValue));
        });

        // Add a listener to the max range slider to update the label dynamically
        maxRangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxValue = newValue.doubleValue();
            maxRangeLabel.setText(String.format("Max: %.0f", maxValue));
        });
    }

    @FXML
    private void handleRefresh() {
        try {
            // Fetch all Quartiers from the database
            ObservableList<Quartier> quartiers = FXCollections.observableArrayList(quartierService.getAllQuartiers());

            // Set the data in the table
            quartierTable.setItems(quartiers);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while fetching Quartiers: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        try {
            // Fetch all Quartiers from the database
            ObservableList<Quartier> quartiers = FXCollections.observableArrayList(quartierService.getAllQuartiers());

            // Filter the Quartiers based on the consommation range
            ObservableList<Quartier> filteredQuartiers = quartiers.filtered(quartier ->
                    quartier.getConsomTot() >= minValue && quartier.getConsomTot() <= maxValue
            );

            // Set the filtered data in the table
            quartierTable.setItems(filteredQuartiers);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while fetching Quartiers: " + e.getMessage());
        }
    }

    private void handleSearch(String searchText) {
        try {
            // Fetch all Quartiers from the database
            ObservableList<Quartier> quartiers = FXCollections.observableArrayList(quartierService.getAllQuartiers());

            // Filter the Quartiers based on the search text
            ObservableList<Quartier> filteredQuartiers = quartiers.filtered(quartier ->
                    quartier.getNom().toLowerCase().startsWith(searchText.toLowerCase())
            );

            // Set the filtered data in the table
            quartierTable.setItems(filteredQuartiers);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while fetching Quartiers: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        // Get the selected Quartier
        Quartier selectedQuartier = quartierTable.getSelectionModel().getSelectedItem();

        if (selectedQuartier == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a Quartier to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected Quartier?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the selected Quartier from the database
                quartierService.deleteQuartier(selectedQuartier.getId());

                // Refresh the table
                handleRefresh();

                showAlert(AlertType.INFORMATION, "Success", "Quartier deleted successfully!");
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "An error occurred while deleting the Quartier: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdate() {
        // Get the selected Quartier
        Quartier selectedQuartier = quartierTable.getSelectionModel().getSelectedItem();

        if (selectedQuartier == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a Quartier to update.");
            return;
        }

        try {
            // Load the update-quartier.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/update-quartier.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            // Get the controller and pass the selected Quartier
            UpdateQuartierController controller = loader.getController();
            controller.setSelectedQuartier(selectedQuartier);
            controller.setStage(stage);

            // Show the pop-up window
            stage.showAndWait();

            // Refresh the table after updating
            handleRefresh();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to load the update window: " + e.getMessage());
        }
    }
    @FXML
    private void handleGenerateQuartierPDF() {
        try {
            List<Quartier> quartiers = quartierService.getAllQuartiers();
            String filePath = "quartiers_report.pdf";
            PDFGenerator.generateQuartierPDF(quartiers, filePath);
            showAlert(AlertType.INFORMATION, "Success", "PDF generated successfully at: " + filePath);
        } catch (SQLException | IOException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to generate PDF: " + e.getMessage());
        }
    }
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}