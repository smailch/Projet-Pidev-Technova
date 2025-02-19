package controllers;

import entities.DeclarationRevenues;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.DeclarationRevenuesService;
import services.SessionManager;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeclarationRevenuesController {

    @FXML
    private TableView<DeclarationRevenues> tableDeclarations;
    @FXML
    private TableColumn<DeclarationRevenues, String> colMontantRevenu;
    @FXML
    private TableColumn<DeclarationRevenues, String> colSourceRevenu;
    @FXML
    private TableColumn<DeclarationRevenues, String> colDateDeclaration;
    @FXML
    private TableColumn<DeclarationRevenues, String> colPreuveRevenu;

    @FXML
    private TextField txtMontantRevenu;
    @FXML
    private TextField txtSourceRevenu;
    @FXML
    private TextField txtPreuveRevenu;
    @FXML
    private TextField searchField;

    private final DeclarationRevenuesService declarationRevenuesService = new DeclarationRevenuesService();
    private final ObservableList<DeclarationRevenues> allDeclarations = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @FXML
    public void initialize() {
        loadDeclarationData();

        // Handle search field input
        searchField.setOnKeyReleased(this::handleSearch);

        // Handle table selection changes
        tableDeclarations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Ensure MontantRevenu is properly formatted and only contains numbers and dots
                Double montantRevenu = newSelection.getMontantRevenu();
                txtMontantRevenu.setText(montantRevenu != null
                        ? String.valueOf(montantRevenu)
                        : ""); // Default to empty if null


                // Ensure SourceRevenu contains only alphabetic characters
                txtSourceRevenu.setText(newSelection.getSourceRevenu() != null
                        ? newSelection.getSourceRevenu().replaceAll("[^a-zA-Z ]", "")
                        : "");

                // Display the proof document path
                txtPreuveRevenu.setText(newSelection.getPreuveRevenu() != null
                        ? newSelection.getPreuveRevenu()
                        : "");
            }
        });

        // Add input validation for MontantRevenu (only numbers and dots allowed)
        txtMontantRevenu.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtMontantRevenu.setText(oldValue);
            }
        });

        // Add input validation for SourceRevenu (only alphabetic characters allowed)
        txtSourceRevenu.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z ]*")) {
                txtSourceRevenu.setText(oldValue);
            }
        });
    }

    private void loadDeclarationData() {
        List<DeclarationRevenues> declarations = declarationRevenuesService.getAllData();
        allDeclarations.setAll(declarations);

        colMontantRevenu.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMontantRevenu())));
        colSourceRevenu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSourceRevenu()));
        colDateDeclaration.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateDeclaration()));
        colPreuveRevenu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreuveRevenu()));

        tableDeclarations.setItems(allDeclarations);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        filterData(searchField.getText().trim().toLowerCase());
    }

    private void filterData(String query) {
        ObservableList<DeclarationRevenues> filteredList = FXCollections.observableArrayList();
        for (DeclarationRevenues declaration : allDeclarations) {
            if (String.valueOf(declaration.getMontantRevenu()).contains(query) ||
                    declaration.getSourceRevenu().toLowerCase().contains(query) ||
                    declaration.getDateDeclaration().contains(query)) {
                filteredList.add(declaration);
            }
        }
        tableDeclarations.setItems(filteredList);
    }

    @FXML
    public void addDeclaration() {
        int idUser = SessionManager.getUserId();
        double montantRevenu = Double.parseDouble(txtMontantRevenu.getText().trim());
        String sourceRevenu = txtSourceRevenu.getText().trim();
        String dateDeclaration = LocalDate.now().format(dateFormatter);
        String preuveRevenu = txtPreuveRevenu.getText().trim();

        if (sourceRevenu.isEmpty() || preuveRevenu.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont requis.", Alert.AlertType.ERROR);
            return;
        }

        DeclarationRevenues newDeclaration = new DeclarationRevenues(0, 12, montantRevenu, sourceRevenu, dateDeclaration, preuveRevenu);
        declarationRevenuesService.addEntity(newDeclaration);
        loadDeclarationData();
        clearFields();
    }

    @FXML
    public void updateDeclaration() {
        DeclarationRevenues selectedDeclaration = tableDeclarations.getSelectionModel().getSelectedItem();
        if (selectedDeclaration != null) {
            try {
                // Validate Montant Revenu
                String montantRevenuStr = txtMontantRevenu.getText().trim();
                if (montantRevenuStr.isEmpty()) {
                    showAlert("Montant requis", "Le montant du revenu est requis.", Alert.AlertType.ERROR);
                    return;
                }
                double montantRevenu = Double.parseDouble(montantRevenuStr);
                if (montantRevenu < 0) {
                    showAlert("Valeur invalide", "Le montant du revenu ne peut pas être négatif.", Alert.AlertType.ERROR);
                    return;
                }

                // Validate Source Revenu
                String sourceRevenu = txtSourceRevenu.getText().trim();
                if (sourceRevenu.isEmpty()) {
                    showAlert("Source requise", "La source du revenu est requise.", Alert.AlertType.ERROR);
                    return;
                }

                // Validate Preuve Revenu
                String preuveRevenu = txtPreuveRevenu.getText().trim();
                if (preuveRevenu.isEmpty()) {
                    showAlert("Preuve requise", "La preuve du revenu est requise.", Alert.AlertType.ERROR);
                    return;
                }

                // Handle file selection if a file was chosen
                if (preuveRevenu.equals("Parcourir")) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Image", "*.jpg", "*.png"));
                    File selectedFile = fileChooser.showOpenDialog(null);
                    if (selectedFile != null) {
                        preuveRevenu = selectedFile.getAbsolutePath(); // Store the selected file's path
                    } else {
                        showAlert("Erreur", "Aucun fichier n'a été sélectionné.", Alert.AlertType.ERROR);
                        return;
                    }
                }

                // Validate Date de Déclaration


                selectedDeclaration.setMontantRevenu(montantRevenu);
                selectedDeclaration.setSourceRevenu(sourceRevenu);
                selectedDeclaration.setPreuveRevenu(preuveRevenu);

                // Update entity
                declarationRevenuesService.updateEntity(selectedDeclaration);
                loadDeclarationData();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Erreur de format", "Veuillez saisir des valeurs numériques valides pour les champs numériques.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une déclaration à mettre à jour.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void deleteDeclaration() {
        DeclarationRevenues selectedDeclaration = tableDeclarations.getSelectionModel().getSelectedItem();
        if (selectedDeclaration != null) {
            declarationRevenuesService.deleteEntity(selectedDeclaration);
            loadDeclarationData();
            clearFields();
        }
    }

    public void searchButton() {
        searchField.clear();
        loadDeclarationData();
        clearFields();
    }

    @FXML
    private void clearFields() {
        txtMontantRevenu.clear();
        txtSourceRevenu.clear();
        txtPreuveRevenu.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez un fichier");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        Window stage = txtPreuveRevenu.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            txtPreuveRevenu.setText(selectedFile.getAbsolutePath());
        }
    }
    @FXML
    public void redirectToFiscale(Event event){
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DossierFiscale.fxml", currentStage, titleBar);
    }
}
