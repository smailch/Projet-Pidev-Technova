package com.example.demo.controllers;

import com.example.demo.models.Quartier;
import com.example.demo.services.QuartierService;
import com.example.demo.services.LampadaireService;
import com.example.demo.utils.PDFGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import com.example.demo.models.Lampadaire;

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
    private double maxValue = 500;

    // Store the original list of quartiers to avoid repeated database queries
    private ObservableList<Quartier> allQuartiers = FXCollections.observableArrayList();

    private QuartierService quartierService = new QuartierService();
    private LampadaireService lampadaireService = new LampadaireService();

    @FXML
    public void initialize() {
        // Load data when the view is initialized
        loadQuartiersFromDatabase();

        // Add a listener to the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterQuartiers();
        });

        // Add a listener to the min range slider to update the label dynamically
        // and apply filter automatically
        minRangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            minValue = newValue.doubleValue();
            minRangeLabel.setText(String.format("Min: %.0f", minValue));
            filterQuartiers(); // Apply filter automatically
        });

        // Add a listener to the max range slider to update the label dynamically
        // and apply filter automatically
        maxRangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxValue = newValue.doubleValue();
            maxRangeLabel.setText(String.format("Max: %.0f", maxValue));
            filterQuartiers(); // Apply filter automatically
        });

        // Add double-click handler to open map view
        quartierTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Quartier selectedQuartier = quartierTable.getSelectionModel().getSelectedItem();
                if (selectedQuartier != null) {
                    openMapForQuartier(selectedQuartier);
                }
            }
        });

        // Add context menu for right-click
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewOnMapItem = new MenuItem("View Streetlights on Map");
        viewOnMapItem.setOnAction(e -> {
            Quartier selectedQuartier = quartierTable.getSelectionModel().getSelectedItem();
            if (selectedQuartier != null) {
                openMapForQuartier(selectedQuartier);
            }
        });
        contextMenu.getItems().add(viewOnMapItem);
        quartierTable.setContextMenu(contextMenu);

        // Add a button directly to the table cell for viewing on map
        addViewMapButton();
    }

    private void addViewMapButton() {
        TableColumn<Quartier, Void> mapColumn = new TableColumn<>("Map");
        mapColumn.setPrefWidth(80);

        mapColumn.setCellFactory(param -> new TableCell<Quartier, Void>() {
            private final Button mapButton = new Button("View Map");
            {
                mapButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                mapButton.setOnAction(event -> {
                    Quartier quartier = getTableView().getItems().get(getIndex());
                    openMapForQuartier(quartier);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(mapButton);
                }
            }
        });

        quartierTable.getColumns().add(mapColumn);
    }

    private void openMapForQuartier(Quartier quartier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/quartier-map-view.fxml"));
            Stage mapStage = new Stage();
            Scene scene = new Scene(loader.load());

            // Get the controller and set the quartier
            QuartierMapViewController controller = loader.getController();
            controller.setQuartier(quartier);

            // Configure and show the map window
            mapStage.setTitle("Streetlights in " + quartier.getNom());
            mapStage.setScene(scene);
            mapStage.initModality(Modality.APPLICATION_MODAL);
            mapStage.show();

            // Log user activity
            System.out.println("Current Date and Time (UTC): 2025-03-01 15:50:33");
            System.out.println("Viewed map for district: " + quartier.getNom());
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to load the map view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLightTheme() {
        quartierTable.getScene().getStylesheets().clear();
        quartierTable.getScene().getStylesheets().add(getClass().getResource("/com/example/demo/light-theme.css").toExternalForm());
    }

    @FXML
    private void switchToDarkTheme() {
        quartierTable.getScene().getStylesheets().clear();
        quartierTable.getScene().getStylesheets().add(getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm());
    }

    @FXML
    private void handleRefresh() {
        // Reload data from database
        loadQuartiersFromDatabase();
        // Apply current filters
        filterQuartiers();
    }

    /**
     * Load all quartiers from database and store in the allQuartiers list
     * Calculate the number of lamps for each quartier
     */
    private void loadQuartiersFromDatabase() {
        try {
            // Fetch all Quartiers from the database
            List<Quartier> quartiers = quartierService.getAllQuartiers();

            // Fetch all Lampadaires
            List<Lampadaire> lampadaires = lampadaireService.getAllLampadaires();

            // Create a map to count lamps per quartier
            Map<Integer, Integer> quartierLampCount = new HashMap<>();

            // Count lamps for each quartier
            for (Lampadaire lamp : lampadaires) {
                int quartierId = lamp.getId_quartier();
                quartierLampCount.put(quartierId, quartierLampCount.getOrDefault(quartierId, 0) + 1);
            }

            // Set the lamp count for each quartier
            for (Quartier quartier : quartiers) {
                int lampCount = quartierLampCount.getOrDefault(quartier.getId(), 0);
                quartier.setNbLamp(lampCount);
            }

            // Update the observable list
            allQuartiers = FXCollections.observableArrayList(quartiers);

            // Set the data in the table
            quartierTable.setItems(allQuartiers);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while fetching data: " + e.getMessage());
        }
    }

    /**
     * Filter quartiers based on current search text and slider values
     */
    private void filterQuartiers() {
        String searchText = searchField.getText().toLowerCase().trim();

        // Start with all quartiers
        ObservableList<Quartier> filteredQuartiers = FXCollections.observableArrayList(allQuartiers);

        // Apply search filter if search text is not empty
        if (!searchText.isEmpty()) {
            filteredQuartiers = filteredQuartiers.filtered(quartier ->
                    quartier.getNom().toLowerCase().contains(searchText)
            );
        }

        // Apply consumption range filter
        filteredQuartiers = filteredQuartiers.filtered(quartier ->
                quartier.getConsomTot() >= minValue && quartier.getConsomTot() <= maxValue
        );

        // Update the table with filtered results
        quartierTable.setItems(filteredQuartiers);

        // Log the filtering activity
        System.out.println("Current Date and Time (UTC): 2025-03-01 15:50:33");
        System.out.println("Filtered quartiers - Range: " + minValue + " to " + maxValue +
                ", Search: '" + searchText + "', Results: " + filteredQuartiers.size());
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
            // Get current quartiers (filtered or all)
            List<Quartier> quartiers = new ArrayList<>(quartierTable.getItems());

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "quartiers_report_" + timestamp + ".pdf";

            // Create file with absolute path
            File pdfFile = new File(fileName).getAbsoluteFile();
            String filePath = pdfFile.getPath();

            // Generate the PDF
            PDFGenerator.generateQuartierPDF(quartiers, filePath);

            // Automatically open the PDF file
            try {
                openPDFFile(pdfFile);
                showAlert(AlertType.INFORMATION, "Success", "PDF generated and opened: " + filePath);
            } catch (Exception e) {
                // If opening fails, still show success for generation but with a note
                showAlert(AlertType.INFORMATION, "Success", "PDF generated successfully at: " + filePath +
                        "\n\nNote: Could not automatically open the file. Please open it manually.");
                System.out.println("Failed to open PDF automatically: " + e.getMessage());
            }
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Opens a PDF file with the system's default PDF viewer
     * @param file The PDF file to open
     */
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
        System.out.println("Current Date and Time (UTC): " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Action: Opened PDF file with system viewer: " + file.getPath());
    }

    @FXML
    private void handleBackToMenu(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/main.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to load the menu: " + e.getMessage());
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