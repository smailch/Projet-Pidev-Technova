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
    private TextField txtStatus;
    @FXML
    private DatePicker dpDateCreation;
    @FXML
    private TextField txtMoyenPaiement;
    @FXML
    private TextField searchField;

    private final DossierFiscaleService dossierService = new DossierFiscaleService();
    private final ObservableList<DossierFiscale> allDossiers = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        loadDossierData();
        searchField.setOnKeyReleased(this::handleSearch);
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
        int idUser = SessionManager.getUserId();
        int anneeFiscale = Integer.parseInt(txtAnneeFiscale.getText().trim());
        double totalImpot = Double.parseDouble(txtTotalImpot.getText().trim());
        double totalImpotPaye = Double.parseDouble(txtTotalImpotPaye.getText().trim());
        String status = txtStatus.getText().trim();
        String moyenPaiement = txtMoyenPaiement.getText().trim();
        String dateCreation = (dpDateCreation.getValue() != null) ? dpDateCreation.getValue().format(dateFormatter) : "";

        if (status.isEmpty() || moyenPaiement.isEmpty() || dateCreation.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont requis.", Alert.AlertType.ERROR);
            return;
        }

        DossierFiscale newDossier = new DossierFiscale(0, 16, anneeFiscale, totalImpot, totalImpotPaye, status, dateCreation, moyenPaiement);
        dossierService.addEntity(newDossier);
        loadDossierData();
        clearFields();
    }

    @FXML
    public void updateDossier() {
        DossierFiscale selectedDossier = tableDossiers.getSelectionModel().getSelectedItem();
        if (selectedDossier != null) {
            try {
                // Validate Année Fiscale
                String anneeFiscaleStr = txtAnneeFiscale.getText().trim();
                if (anneeFiscaleStr.isEmpty()) {
                    showAlert("Année requise", "L'année fiscale est requise.",Alert.AlertType.ERROR);
                    return;
                }
                int anneeFiscale = Integer.parseInt(anneeFiscaleStr);
                if (anneeFiscale < 2000 || anneeFiscale > LocalDate.now().getYear()) {
                    showAlert("Année invalide", "L'année fiscale doit être entre 2000 et l'année en cours.",Alert.AlertType.ERROR);
                    return;
                }

                // Validate Total Impôt
                String totalImpotStr = txtTotalImpot.getText().trim();
                if (totalImpotStr.isEmpty()) {
                    showAlert("Total impôt requis", "Le total de l'impôt est requis.", Alert.AlertType.ERROR);
                    return;
                }
                double totalImpot = Double.parseDouble(totalImpotStr);
                if (totalImpot < 0) {
                    showAlert("Valeur invalide", "Le total de l'impôt ne peut pas être négatif.",Alert.AlertType.ERROR);
                    return;
                }

                // Validate Total Impôt Payé
                String totalImpotPayeStr = txtTotalImpotPaye.getText().trim();
                if (totalImpotPayeStr.isEmpty()) {
                    showAlert("Montant payé requis", "Le montant de l'impôt payé est requis.",Alert.AlertType.ERROR);
                    return;
                }
                double totalImpotPaye = Double.parseDouble(totalImpotPayeStr);
                if (totalImpotPaye < 0 || totalImpotPaye > totalImpot) {
                    showAlert("Valeur invalide", "L'impôt payé ne peut pas être négatif ou supérieur au total.",Alert.AlertType.ERROR);
                    return;
                }

                // Validate Statut
                String status = txtStatus.getText().trim();
                if (status.isEmpty()) {
                    showAlert("Statut requis", "Le statut est requis.",Alert.AlertType.ERROR);
                    return;
                }

                // Validate Moyen de Paiement
                String moyenPaiement = txtMoyenPaiement.getText().trim();
                if (moyenPaiement.isEmpty()) {
                    showAlert("Moyen de paiement requis", "Le moyen de paiement est requis.",Alert.AlertType.ERROR);
                    return;
                }

                // Validate Date de Création
                String dateCreation = (dpDateCreation.getValue() != null) ? dpDateCreation.getValue().format(dateFormatter) : "";
                if (dateCreation.isEmpty()) {
                    showAlert("Date requise", "La date de création est requise.",Alert.AlertType.ERROR);
                    return;
                }
                if (dpDateCreation.getValue().isAfter(LocalDate.now())) {
                    showAlert("Date future", "La date de création ne peut pas être future.",Alert.AlertType.ERROR);
                    return;
                }

                // Set validated values to the object
                selectedDossier.setAnneeFiscale(anneeFiscale);
                selectedDossier.setTotalImpot(totalImpot);
                selectedDossier.setTotalImpotPaye(totalImpotPaye);
                selectedDossier.setStatus(status);
                selectedDossier.setMoyenPaiement(moyenPaiement);
                selectedDossier.setDateCreation(dateCreation);

                // Update entity
                dossierService.updateEntity(selectedDossier);
                loadDossierData();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Erreur de format","Veuillez saisir des valeurs numériques valides pour les champs numériques.",Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un dossier à mettre à jour.",Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void deleteDossier() {
        DossierFiscale selectedDossier = tableDossiers.getSelectionModel().getSelectedItem();
        if (selectedDossier != null) {
            dossierService.deleteEntity(selectedDossier);
            loadDossierData();
            clearFields();
        }
    }
    public void searchButton() {
        searchField.clear();
        loadDossierData();
        clearFields();
    }

    @FXML
    private void clearFields() {
        txtAnneeFiscale.clear();
        txtTotalImpot.clear();
        txtTotalImpotPaye.clear();
        txtStatus.clear();
        dpDateCreation.setValue(null);
        txtMoyenPaiement.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
