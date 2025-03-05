package controllers;

import entities.DossierFiscale;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.DossierFiscaleService;
import services.SessionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class DossierFiscaleUserController {

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
    private TextField searchField;

    private final DossierFiscaleService dossierService = new DossierFiscaleService();
    private final ObservableList<DossierFiscale> allDossiers = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        loadDossierDataUser();

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
    private void loadDossierDataUser() {
        List<DossierFiscale> dossiers = dossierService.getAllDataUser();

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













    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    @FXML
    public void searchButton() {
        searchField.clear();
        loadDossierData();
    }



    @FXML
    public void HandlePayment(javafx.event.ActionEvent actionEvent) {
        DossierFiscale selectedDossier = tableDossiers.getSelectionModel().getSelectedItem();

        // Check if a dossier is selected
        if (selectedDossier == null) {
            // Show alert if no dossier is selected
            showAlert("Aucun dossier sélectionné", "Veuillez sélectionner un dossier pour continuer.", Alert.AlertType.WARNING);
        } else {
            // Check if totalImpot equals totalImpotPaye
            if (selectedDossier.getTotalImpot() == selectedDossier.getTotalImpotPaye()) {
                // Show alert if they are equal
                showAlert("Paiement déjà effectué", "Le total de l'impôt est déjà payé. Aucun paiement supplémentaire n'est nécessaire.", Alert.AlertType.INFORMATION);
            } else {
                int selectedDossierId = selectedDossier.getId();
                SessionManager.getInstance().setDossierId(selectedDossierId);
                System.out.println(SessionManager.getInstance().getDossierId());
                Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
                NavigationUtils.switchPage("/form.fxml", currentStage, titleBar);
            }
        }
    }

}
