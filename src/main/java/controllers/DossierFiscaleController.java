package controllers;

import entities.DossierFiscale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import services.DossierFiscaleService;
import services.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DossierFiscaleController {

    @FXML
    private TableView<DossierFiscale> tableDossiers;
    @FXML
    private TableColumn<DossierFiscale, String> colAnneeFiscale;
    @FXML
    private TableColumn<DossierFiscale, String> colTotalImpot;
    @FXML
    private TableColumn<DossierFiscale, String> colTotalImpotPaye;
    @FXML
    private TableColumn<DossierFiscale, String> colStatus;
    @FXML
    private TableColumn<DossierFiscale, String> colDateCreation;
    @FXML
    private TableColumn<DossierFiscale, String> colMoyenPaiement;

    @FXML
    private TextField txtAnneeFiscale;
    @FXML
    private TextField txtTotalImpot;
    @FXML
    private TextField txtTotalImpotPaye;
    @FXML
    private ComboBox<String> comboStatus; // Changed from TextField to ComboBox
    @FXML
    private ComboBox<String> comboMoyenPaiement; // Changed from TextField to ComboBox
    @FXML
    private TextField searchField;

    private final DossierFiscaleService dossierService = new DossierFiscaleService();
    private final ObservableList<DossierFiscale> allDossiers = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        loadDossierData();

        searchField.setOnKeyReleased(this::handleSearch);

        tableDossiers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFieldsFromSelectedDossier(newSelection);
            }
        });

        applyInputFilters();

        // Initialize the ComboBoxes
        comboStatus.setItems(FXCollections.observableArrayList("actif", "Inactif", "Suspendu"));
        comboMoyenPaiement.setItems(FXCollections.observableArrayList("Virement Bancaire", "Especes"));
    }

    private void applyInputFilters() {
        // Apply filters for numeric fields
        txtAnneeFiscale.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        txtTotalImpot.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));

        txtTotalImpotPaye.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        }));
    }

    private void populateFieldsFromSelectedDossier(DossierFiscale dossier) {
        txtAnneeFiscale.setText(String.valueOf(dossier.getAnneeFiscale()));
        txtTotalImpot.setText(String.valueOf(dossier.getTotalImpot()));
        txtTotalImpotPaye.setText(String.valueOf(dossier.getTotalImpotPaye()));
        comboStatus.setValue(dossier.getStatus()); // Changed from txtStatus
        comboMoyenPaiement.setValue(dossier.getMoyenPaiement()); // Changed from txtMoyenPaiement
    }

    private void loadDossierData() {
        List<DossierFiscale> dossiers = dossierService.getAllData();
        allDossiers.setAll(dossiers);

        colAnneeFiscale.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAnneeFiscale())));
        colTotalImpot.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalImpot())));
        colTotalImpotPaye.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalImpotPaye())));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colDateCreation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateCreation()));
        colMoyenPaiement.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMoyenPaiement()));

        tableDossiers.setItems(allDossiers);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        filterData(searchField.getText().trim().toLowerCase());
    }

    private void filterData(String query) {
        ObservableList<DossierFiscale> filteredList = FXCollections.observableArrayList();
        for (DossierFiscale dossier : allDossiers) {
            if (String.valueOf(dossier.getAnneeFiscale()).contains(query) ||
                    dossier.getStatus().toLowerCase().contains(query) ||
                    dossier.getDateCreation().contains(query)) {
                filteredList.add(dossier);
            }
        }
        tableDossiers.setItems(filteredList);
    }

    @FXML
    public void addDossier() {
        if (validateInputs()) {
            DossierFiscale newDossier = createDossierFromFields();
            dossierService.addEntity(newDossier);
            loadDossierData();
            clearFields();
        }
    }

    @FXML
    public void updateDossier() {
        DossierFiscale selectedDossier = tableDossiers.getSelectionModel().getSelectedItem();
        if (selectedDossier != null && validateInputs()) {
            updateSelectedDossier(selectedDossier);
            dossierService.updateEntity(selectedDossier);
            loadDossierData();
            clearFields();
        } else {
            showAlert("Sélectionnez un dossier", "Veuillez sélectionner un dossier à mettre à jour.", Alert.AlertType.ERROR);
        }
    }

    private boolean validateInputs() {
        if (txtAnneeFiscale.getText().isEmpty() || txtTotalImpot.getText().isEmpty() || txtTotalImpotPaye.getText().isEmpty() ||
                comboStatus.getValue() == null || comboMoyenPaiement.getValue() == null) { // Changed validation for ComboBox
            showAlert("Erreur", "Tous les champs sont requis.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private DossierFiscale createDossierFromFields() {
        int anneeFiscale = Integer.parseInt(txtAnneeFiscale.getText().trim());
        double totalImpot = Double.parseDouble(txtTotalImpot.getText().trim());
        double totalImpotPaye = Double.parseDouble(txtTotalImpotPaye.getText().trim());
        String status = comboStatus.getValue().trim(); // Changed to get value from ComboBox
        String moyenPaiement = comboMoyenPaiement.getValue().trim(); // Changed to get value from ComboBox
        String dateCreation = LocalDate.now().format(dateFormatter);

        return new DossierFiscale(0, SessionManager.getUserId(), anneeFiscale, totalImpot, totalImpotPaye, status, dateCreation, moyenPaiement);
    }

    private void updateSelectedDossier(DossierFiscale selectedDossier) {
        selectedDossier.setAnneeFiscale(Integer.parseInt(txtAnneeFiscale.getText().trim()));
        selectedDossier.setTotalImpot(Double.parseDouble(txtTotalImpot.getText().trim()));
        selectedDossier.setTotalImpotPaye(Double.parseDouble(txtTotalImpotPaye.getText().trim()));
        selectedDossier.setStatus(comboStatus.getValue().trim()); // Changed to get value from ComboBox
        selectedDossier.setMoyenPaiement(comboMoyenPaiement.getValue().trim()); // Changed to get value from ComboBox
        selectedDossier.setDateCreation(LocalDate.now().format(dateFormatter));
    }

    @FXML
    public void deleteDossier() {
        DossierFiscale selectedDossier = tableDossiers.getSelectionModel().getSelectedItem();
        if (selectedDossier != null) {
            dossierService.deleteEntity(selectedDossier);
            loadDossierData();
        } else {
            showAlert("Sélectionnez un dossier", "Veuillez sélectionner un dossier à supprimer.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void clearFields() {
        txtAnneeFiscale.clear();
        txtTotalImpot.clear();
        txtTotalImpotPaye.clear();
        comboStatus.setValue(null); // Clear ComboBox
        comboMoyenPaiement.setValue(null); // Clear ComboBox
    }

    @FXML
    public void searchButton() {
        searchField.clear();
        loadDossierData();
        clearFields();
    }
}
