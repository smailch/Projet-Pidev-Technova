package controllers;

import entities.DeclarationRevenues;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import services.DeclarationRevenuesService;
import services.SessionManager;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private DatePicker dpDateDeclaration;
    @FXML
    private TextField searchField;

    private final DeclarationRevenuesService declarationRevenuesService = new DeclarationRevenuesService();
    private final ObservableList<DeclarationRevenues> allDeclarations = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        loadDeclarationData();

        // Set up search functionality
        searchField.setOnKeyReleased(this::handleSearch);

        // Handle table row selection
        tableDeclarations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Populate the form with the selected row's data
                txtMontantRevenu.setText(String.valueOf(newSelection.getMontantRevenu()));
                txtSourceRevenu.setText(newSelection.getSourceRevenu());
                txtPreuveRevenu.setText(newSelection.getPreuveRevenu());

                // Handle the DatePicker value safely
                String dateDeclaration = newSelection.getDateDeclaration();
                if (dateDeclaration != null && !dateDeclaration.isEmpty()) {
                    try {
                        dpDateDeclaration.setValue(LocalDate.parse(dateDeclaration, dateFormatter));
                    } catch (DateTimeParseException e) {
                        dpDateDeclaration.setValue(null); // Handle parsing error
                    }
                } else {
                    dpDateDeclaration.setValue(null);
                }
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
        String dateDeclaration = (dpDateDeclaration.getValue() != null) ? dpDateDeclaration.getValue().format(dateFormatter) : "";
        String preuveRevenu = txtPreuveRevenu.getText().trim();

        if (sourceRevenu.isEmpty() || dateDeclaration.isEmpty() || preuveRevenu.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont requis.", Alert.AlertType.ERROR);
            return;
        }

        DeclarationRevenues newDeclaration = new DeclarationRevenues(0, 4, montantRevenu, sourceRevenu, dateDeclaration, preuveRevenu);
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
                String dateDeclaration = (dpDateDeclaration.getValue() != null) ? dpDateDeclaration.getValue().format(dateFormatter) : "";
                if (dateDeclaration.isEmpty()) {
                    showAlert("Date requise", "La date de déclaration est requise.", Alert.AlertType.ERROR);
                    return;
                }
                if (dpDateDeclaration.getValue().isAfter(LocalDate.now())) {
                    showAlert("Date future", "La date de déclaration ne peut pas être future.", Alert.AlertType.ERROR);
                    return;
                }

                selectedDeclaration.setMontantRevenu(montantRevenu);
                selectedDeclaration.setSourceRevenu(sourceRevenu);
                selectedDeclaration.setPreuveRevenu(preuveRevenu); // Update with the file path
                selectedDeclaration.setDateDeclaration(dateDeclaration);

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
        dpDateDeclaration.setValue(null);
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
}
