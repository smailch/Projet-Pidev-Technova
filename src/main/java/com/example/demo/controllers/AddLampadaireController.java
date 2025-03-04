    package com.example.demo.controllers;

    import com.example.demo.models.Lampadaire;
    import com.example.demo.models.Quartier;
    import com.example.demo.services.LampadaireService;
    import com.example.demo.services.QuartierService;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.layout.StackPane;
    import javafx.stage.Stage;

    import java.io.IOException;
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
        @FXML
        private ToggleButton darkModeToggle;
        @FXML
        private StackPane rootPane;

        private LampadaireService lampadaireService = new LampadaireService();
        private QuartierService quartierService = new QuartierService();
        private Lampadaire currentLampadaire;  // For edit mode

        // Store coordinates separately from the location name
        private double selectedLat = 0.0;
        private double selectedLng = 0.0;

        @FXML
        public void initialize() {
            Platform.runLater(() -> {
                if (localisationField.getScene() != null) {
                    String css = getClass().getResource("/com/example/demo/modern-purple.css").toExternalForm();
                    localisationField.getScene().getStylesheets().clear();
                    localisationField.getScene().getStylesheets().add(css);
                }
            });
            populateQuartierComboBox();
            setupDarkMode();
            setupValidation();

            // Initialize date picker to current date if not in edit mode
            if (dateInstallationField.getValue() == null) {
                dateInstallationField.setValue(LocalDate.now());
            }
        }

        private void setupValidation() {
            // Add numeric validation to consumption field
            consommationField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()) {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        consommationField.setText(oldValue);
                    }
                }
            });
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

                quartierComboBox.getItems().stream()
                        .filter(q -> q.getId() == currentLampadaire.getId_quartier())
                        .findFirst()
                        .ifPresent(quartierComboBox::setValue);
            }
        }

        // Updated to accept place name from the map
        public void setLocation(double lat, double lng, String placeName) {
            this.selectedLat = lat;
            this.selectedLng = lng;
            localisationField.setText(placeName);
        }

        // Keep the old method for compatibility
        public void setLocation(double lat, double lng) {
            setLocation(lat, lng, lat + ", " + lng);
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
                e.printStackTrace();
            }
        }

        @FXML
        private void handleSave() {
            try {
                if (validateFields()) {
                    Lampadaire lampadaire = createLampadaireFromFields();
                    Quartier selectedQuartier = quartierComboBox.getValue();

                    // Transaction: save lampadaire and update quartier in one logical operation
                    boolean isNewLampadaire = (currentLampadaire == null);

                    if (isNewLampadaire) {
                        lampadaireService.createLampadaire(lampadaire);

                        // Update the quartier statistics for the new lampadaire
                        selectedQuartier.setNbLamp(selectedQuartier.getNbLamp() + 1);
                        selectedQuartier.setConsomTot(selectedQuartier.getConsomTot() + lampadaire.getConsommation());
                    } else {
                        // For update, we need to handle the case where the neighborhood might have changed
                        double oldConsommation = currentLampadaire.getConsommation();
                        int oldQuartierId = currentLampadaire.getId_quartier();

                        lampadaire.setId(currentLampadaire.getId());
                        lampadaireService.updateLampadaire(lampadaire);

                        // If the neighborhood hasn't changed
                        if (oldQuartierId == selectedQuartier.getId()) {
                            // Just update the consumption difference
                            selectedQuartier.setConsomTot(selectedQuartier.getConsomTot() - oldConsommation + lampadaire.getConsommation());
                        } else {
                            // Neighborhood changed - update both old and new neighborhoods
                            selectedQuartier.setNbLamp(selectedQuartier.getNbLamp() + 1);
                            selectedQuartier.setConsomTot(selectedQuartier.getConsomTot() + lampadaire.getConsommation());

                            // Update old neighborhood if it exists
                            Quartier oldQuartier = quartierService.getQuartierById(oldQuartierId);
                            if (oldQuartier != null) {
                                oldQuartier.setNbLamp(oldQuartier.getNbLamp() - 1);
                                oldQuartier.setConsomTot(oldQuartier.getConsomTot() - oldConsommation);
                                quartierService.updateQuartier(oldQuartier);
                            }
                        }
                    }

                    // Update the quartier in the database
                    quartierService.updateQuartier(selectedQuartier);

                    showAlert("Success",
                            isNewLampadaire ? "Streetlight added successfully" : "Streetlight updated successfully",
                            Alert.AlertType.INFORMATION);

                    //redirectToListLampadaires();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter valid numbers for consumption", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Save Error", "Error saving streetlight: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }



        private boolean validateFields() {
            StringBuilder errorMessage = new StringBuilder();

            if (localisationField.getText().isEmpty()) {
                errorMessage.append("- Location is required\n");
            }

            if (consommationField.getText().isEmpty()) {
                errorMessage.append("- Power consumption is required\n");
            } else {
                try {
                    double consumption = Double.parseDouble(consommationField.getText());
                    if (consumption <= 0) {
                        errorMessage.append("- Power consumption must be greater than zero\n");
                    }
                } catch (NumberFormatException e) {
                    errorMessage.append("- Power consumption must be a valid number\n");
                }
            }

            if (quartierComboBox.getValue() == null) {
                errorMessage.append("- District selection is required\n");
            }

            if (dateInstallationField.getValue() == null) {
                errorMessage.append("- Installation date is required\n");
            } else if (dateInstallationField.getValue().isAfter(LocalDate.now())) {
                errorMessage.append("- Installation date cannot be in the future\n");
            }

            if (errorMessage.length() > 0) {
                showAlert("Validation Error", errorMessage.toString(), Alert.AlertType.WARNING);
                return false;
            }

            return true;
        }

        private Lampadaire createLampadaireFromFields() {
            Lampadaire lampadaire = new Lampadaire(
                    0,
                    localisationField.getText(),
                    etatField.isSelected(),
                    Double.parseDouble(consommationField.getText()),
                    quartierComboBox.getValue().getId(),
                    dateInstallationField.getValue()
            );

            return lampadaire;
        }

        @FXML
        private void handleCancel() {
            redirectToMenu();
        }

        @FXML
        private void redirectToMenu() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/main.fxml"));
                Stage stage = (Stage) localisationField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } catch (IOException e) {
                showAlert("Navigation Error", "Error loading menu: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }

        private void showAlert(String title, String message, Alert.AlertType type) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }