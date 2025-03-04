package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.example.demo.services.QuartierService;
import com.example.demo.utils.LampadaireTimeManager;
import com.example.demo.utils.LocationService;
import com.example.demo.utils.PDFGenerator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class LampadaireListController {

    @FXML
    private TableView<Lampadaire> lampadaireTable;

    @FXML
    private TableColumn<Lampadaire, Integer> idColumn;

    @FXML
    private TableColumn<Lampadaire, String> localisationColumn;

    @FXML
    private TableColumn<Lampadaire, Boolean> etatColumn;

    @FXML
    private TableColumn<Lampadaire, Double> consommationColumn;

    @FXML
    private TableColumn<Lampadaire, String> quartierNameColumn;

    @FXML
    private TableColumn<Lampadaire, String> dateInstallationColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    @FXML
    private DatePicker filterDatePicker;

    @FXML
    private ComboBox<String> dateFilterTypeComboBox;

    @FXML
    private TextField locationCheckField;

    private LampadaireService lampadaireService = new LampadaireService();
    private ObservableList<Lampadaire> allLampadaires = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind columns to Lampadaire properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        localisationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        consommationColumn.setCellValueFactory(new PropertyValueFactory<>("consommation"));
        dateInstallationColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate_installation();
            if (date != null) {
                return new SimpleStringProperty(date.toString());
            }
            return new SimpleStringProperty("");
        });

        // Format boolean values in the etatColumn to show as "Active" or "Inactive"
        etatColumn.setCellFactory(column -> new TableCell<Lampadaire, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Active" : "Inactive");
                    // Add color-coding for status
                    if (item) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });

        // Add a new column for Quartier name
        quartierNameColumn.setCellValueFactory(cellData -> {
            int idQuartier = cellData.getValue().getId_quartier();
            QuartierService quartierService = new QuartierService();
            try {
                Quartier quartier = quartierService.getQuartierById(idQuartier);
                if (quartier != null) {
                    return new SimpleStringProperty(quartier.getNom());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("Unknown");
        });

        // Initialize date filter type ComboBox
        dateFilterTypeComboBox.setItems(FXCollections.observableArrayList(
                "Before Date", "After Date", "On Date"
        ));
        dateFilterTypeComboBox.setValue("After Date"); // Default value

        // Add listeners for date filtering
        filterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                applyDateFilter();
            }
        });

        dateFilterTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (filterDatePicker.getValue() != null) {
                applyDateFilter();
            }
        });

        // Load data
        loadAllLampadaires();

        // Set up custom cell factory for status column
        etatColumn.setCellFactory(column -> new TableCell<Lampadaire, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("status-active", "status-inactive");
                } else {
                    if (item) {
                        setText("ACTIVE");
                        getStyleClass().removeAll("status-inactive");
                        getStyleClass().add("status-active");
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setText("INACTIVE");
                        getStyleClass().removeAll("status-active");
                        getStyleClass().add("status-inactive");
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });
    }

    private void loadAllLampadaires() {
        try {
            allLampadaires.clear();
            allLampadaires.addAll(lampadaireService.getAllLampadaires());
            lampadaireTable.setItems(allLampadaires);

            // Log activity
            logActivity("Loaded all lampadaires");
        } catch (Exception e) {
            showAlert("Error", "An error occurred while loading lampadaires: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyDateFilter() {
        LocalDate filterDate = filterDatePicker.getValue();
        if (filterDate == null) {
            lampadaireTable.setItems(allLampadaires);
            return;
        }

        String filterType = dateFilterTypeComboBox.getValue();

        // Convert LocalDate to java.sql.Date for comparison
        Date sqlFilterDate = Date.valueOf(filterDate);

        // Filter the lampadaires based on the selected criteria
        List<Lampadaire> filteredList = allLampadaires.stream()
                .filter(l -> {
                    LocalDate installDate = l.getDate_installation();
                    if (installDate == null) {
                        return false;
                    }

                    Date sqlInstallDate = Date.valueOf(installDate);
                    switch (filterType) {
                        case "Before Date":
                            return sqlInstallDate.before(sqlFilterDate);
                        case "After Date":
                            return sqlInstallDate.after(sqlFilterDate) || sqlInstallDate.equals(sqlFilterDate);
                        case "On Date":
                            return sqlInstallDate.equals(sqlFilterDate);
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());

        lampadaireTable.setItems(FXCollections.observableArrayList(filteredList));

        // Log filtering activity
        logActivity("Filtered lampadaires by date: " + filterType + " " + filterDate);
    }

    @FXML
    private void handleResetFilters() {
        filterDatePicker.setValue(null);
        lampadaireTable.setItems(allLampadaires);
        logActivity("Reset all filters");
    }

    @FXML
    private void handleRefresh() {
        loadAllLampadaires();

        // If there's an active filter, reapply it
        if (filterDatePicker.getValue() != null) {
            applyDateFilter();
        }
    }

    /**
     * Determines if a lamp should be active based on the local time at its geographical location
     * using globally accurate sunrise/sunset calculations
     */
    private boolean shouldLampBeActiveByLocalTime(double latitude, double longitude, String locationName) {
        // Use the LampadaireTimeManager to determine if it's night time
        boolean isNight = LampadaireTimeManager.isNightTime(latitude, longitude);

        // Get a user-friendly description for logging
        String timeDescription = LampadaireTimeManager.getTimeDescription(latitude, longitude, locationName);

        // Log the calculation
        logActivity(timeDescription);

        // Lamp should be active during night time
        return isNight;
    }

    @FXML
    private void handleUpdateLampStatusByTime() {
        try {
            int updatedCount = 0;
            LocationService locationService = new LocationService();

            for (Lampadaire lamp : allLampadaires) {
                // Get the location name
                String locationStr = lamp.getLocalisation();

                // Try to get coordinates either from the string directly or via geocoding
                double[] coords = extractCoordinatesOrGeocode(locationStr, locationService);

                if (coords != null) {
                    // Determine if lamp should be active based on local time at coordinates
                    boolean shouldBeActive = shouldLampBeActiveByLocalTime(coords[0], coords[1], locationStr);

                    // Update lamp status if it's different from current status
                    if (lamp.isEtat() != shouldBeActive) {
                        lamp.setEtat(shouldBeActive);
                        lampadaireService.updateLampadaire(lamp);
                        updatedCount++;

                        // Log the status change
                        logActivity(String.format(
                                "Changed lamp ID: %d at %s to %s based on local time",
                                lamp.getId(),
                                locationStr,
                                shouldBeActive ? "ACTIVE" : "INACTIVE"
                        ));
                    }
                } else {
                    logActivity("Could not determine coordinates for location: " + locationStr);
                }
            }

            // Refresh the table
            handleRefresh();

            showAlert("Success", "Updated " + updatedCount + " lampadaires based on their locations' local time");
            logActivity("Updated " + updatedCount + " lampadaire statuses based on local time");

        } catch (Exception e) {
            showAlert("Error", "Failed to update lamp statuses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Attempts to extract coordinates from a string or use geocoding if needed
     */
    private double[] extractCoordinatesOrGeocode(String location, LocationService locationService) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        // First try to parse as coordinates
        double[] coords = extractCoordinates(location);
        if (coords != null) {
            return coords;
        }

        // If not direct coordinates, try geocoding
        try {
            return locationService.geocodeLocation(location);
        } catch (Exception e) {
            logActivity("Geocoding failed for: " + location + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to extract coordinate values from a string
     */
    private double[] extractCoordinates(String location) {
        if (location == null || location.isEmpty()) {
            return null;
        }

        try {
            // Check if the location contains comma-separated coordinates
            if (location.contains(",")) {
                String[] parts = location.split(",");
                if (parts.length >= 2) {
                    // Try to parse as numbers
                    String latStr = parts[0].trim();
                    String lngStr = parts[1].trim();

                    // Check if these look like valid coordinates
                    if (latStr.matches("-?\\d+(\\.\\d+)?") && lngStr.matches("-?\\d+(\\.\\d+)?")) {
                        double lat = Double.parseDouble(latStr);
                        double lng = Double.parseDouble(lngStr);

                        // Basic validation
                        if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                            return new double[] {lat, lng};
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing coordinates from: " + location);
        }

        return null;
    }

    @FXML
    private void handleDelete() {
        Lampadaire selectedLampadaire = lampadaireTable.getSelectionModel().getSelectedItem();
        if (selectedLampadaire != null) {
            try {
                lampadaireService.deleteLampadaire(selectedLampadaire.getId());
                handleRefresh(); // Refresh the table after deletion
                logActivity("Deleted lampadaire with ID: " + selectedLampadaire.getId());
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting the lampadaire: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a lampadaire to delete.");
        }
    }

    @FXML
    private void handleUpdate() {
        Lampadaire selectedLampadaire = lampadaireTable.getSelectionModel().getSelectedItem();
        if (selectedLampadaire != null) {
            try {
                // Load the update dialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/UpdateLampadaireDialog.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Update Lampadaire");
                dialogStage.initModality(Modality.APPLICATION_MODAL);

                // Set the preferred size of the dialog
                dialogStage.setWidth(800); // Set the width of the dialog
                dialogStage.setHeight(800); // Set the height of the dialog

                // Load the scene
                Scene scene = new Scene(loader.load());
                dialogStage.setScene(scene);

                // Pass the selected Lampadaire to the dialog controller
                UpdateLampadaireDialogController controller = loader.getController();
                controller.setSelectedLampadaire(selectedLampadaire);

                // Show the dialog and wait for it to close
                dialogStage.showAndWait();

                // Refresh the table after updating
                handleRefresh();
                logActivity("Updated lampadaire with ID: " + selectedLampadaire.getId());
            } catch (IOException e) {
                showAlert("Error", "An error occurred while loading the update dialog: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a lampadaire to update.");
        }
    }

    @FXML
    private void handleGeneratePDF() {
        try {
            // Get the current list of lampadaires (filtered or not)
            List<Lampadaire> lampadaires = lampadaireTable.getItems();

            // Demander à l'utilisateur où sauvegarder le fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");

            // Générer un nom de fichier par défaut avec timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String defaultFileName = "lampadaires_list_" + timestamp + ".pdf";
            fileChooser.setInitialFileName(defaultFileName);

            // Définir les extensions
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);

            // Afficher la boîte de dialogue
            Stage stage = (Stage) lampadaireTable.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                String filePath = file.getAbsolutePath();

                // Générer le PDF
                PDFGenerator.generateLampadairePDF(lampadaires, filePath);

                // Log activity
                logActivity("Generated PDF report: " + filePath);

                // Demander à l'utilisateur s'il veut ouvrir le PDF
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("PDF généré");
                alert.setHeaderText("PDF généré avec succès");
                alert.setContentText("Voulez-vous ouvrir le fichier PDF?");

                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

                if (result == ButtonType.YES) {
                    // Ouvrir le PDF
                    try {
                        openPDFFile(file);
                    } catch (Exception e) {
                        showAlert("Attention", "Impossible d'ouvrir automatiquement le PDF: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred while generating the PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openPDFFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getAbsolutePath());
        }

        // Check if Desktop is supported on the platform
        if (!Desktop.isDesktopSupported()) {
            throw new IOException("Desktop is not supported on this platform");
        }

        Desktop desktop = Desktop.getDesktop();

        // Check if the open operation is supported
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            throw new IOException("Opening files is not supported on this platform");
        }

        // Open the file with the default application
        desktop.open(file);
        logActivity("Opened PDF file with system viewer: " + file.getPath());
    }

    @FXML
    private void handleBackToMenu(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.show();
            logActivity("Returned to main menu");
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load the menu: " + e.getMessage());
        }
    }

    private void logActivity(String action) {
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + currentDateTime + "] " + action);
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type;
        if (title.equals("Error")) {
            type = Alert.AlertType.ERROR;
        } else if (title.equals("Warning") || title.equals("No Selection")) {
            type = Alert.AlertType.WARNING;
        } else {
            type = Alert.AlertType.INFORMATION;
        }

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}