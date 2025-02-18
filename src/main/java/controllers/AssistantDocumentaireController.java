package controllers;

import entities.AssistantDocumentaire;
import entities.DocumentAdministratif;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import services.AssistantDocumentaireService;
import services.DocumentAdministratifService;
import services.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AssistantDocumentaireController {

    @FXML
    private TableView<AssistantDocumentaire> tableAssistants;
    @FXML
    private TableColumn<AssistantDocumentaire, String> colTypeAssistance;
    @FXML
    private TableColumn<AssistantDocumentaire, String> colDateDemande;
    @FXML
    private TableColumn<AssistantDocumentaire, String> colStatus;
    @FXML
    private TableColumn<AssistantDocumentaire, String> colRemarque;
    @FXML
    private TableColumn<AssistantDocumentaire, String> colRappelAutomatique;

    @FXML
    private TextField txtTypeAssistance;
    @FXML
    private DatePicker dpDateDemande;
    @FXML
    private TextField txtStatus;
    @FXML
    private TextField txtRemarque;
    @FXML
    private CheckBox chkRappelAutomatique;
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> comboDocument;

    private final AssistantDocumentaireService assistantService = new AssistantDocumentaireService();
    private final ObservableList<AssistantDocumentaire> allAssistants = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DocumentAdministratifService documentService = new DocumentAdministratifService();

    @FXML
    public void initialize() {
        populateComboBox();
        loadAssistantData();
        searchField.setOnKeyReleased(this::handleSearch);

        tableAssistants.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtTypeAssistance.setText(newSelection.getTypeAssistance());

                // Handle DatePicker value safely
                String dateDemande = newSelection.getDateDemande();
                if (dateDemande != null && !dateDemande.isEmpty()) {
                    try {
                        dpDateDemande.setValue(LocalDate.parse(dateDemande, dateFormatter));
                    } catch (DateTimeParseException e) {
                        dpDateDemande.setValue(null);
                    }
                } else {
                    dpDateDemande.setValue(null);
                }

                txtStatus.setText(newSelection.getStatus());
                txtRemarque.setText(newSelection.getRemarque());
                chkRappelAutomatique.setSelected(newSelection.isRappelAutomatique());
            }
        });
    }

    private void loadAssistantData() {
        List<AssistantDocumentaire> assistants = assistantService.getAllData();
        allAssistants.setAll(assistants);

        colTypeAssistance.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeAssistance()));
        colDateDemande.setCellValueFactory(cellData -> {
            String date = cellData.getValue().getDateDemande();
            return new SimpleStringProperty((date != null) ? date : "");
        });
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colRemarque.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemarque()));
        colRappelAutomatique.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isRappelAutomatique() ? "Oui" : "Non"));

        tableAssistants.setItems(allAssistants);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        filterData(searchField.getText().trim().toLowerCase());
    }

    private void filterData(String query) {
        ObservableList<AssistantDocumentaire> filteredList = FXCollections.observableArrayList();
        for (AssistantDocumentaire assistant : allAssistants) {
            String dateDemande = (assistant.getDateDemande() != null) ? assistant.getDateDemande() : "";
            if (assistant.getTypeAssistance().toLowerCase().contains(query) ||
                    dateDemande.toLowerCase().contains(query) ||
                    assistant.getStatus().toLowerCase().contains(query)) {
                filteredList.add(assistant);
            }
        }
        tableAssistants.setItems(filteredList);
    }

    @FXML
    public void addAssistant() {
        int iduser = SessionManager.getUserId();
        String typeAssistance = txtTypeAssistance.getText().trim();
        LocalDate dateDemandeValue = dpDateDemande.getValue();
        String dateDemande = (dateDemandeValue != null) ? dateDemandeValue.format(dateFormatter) : "";
        String status = txtStatus.getText().trim();
        String remarque = txtRemarque.getText().trim();
        boolean rappelAutomatique = chkRappelAutomatique.isSelected();

        // Get selected nomDocument from comboBox
        String selectedNomDocument = comboDocument.getSelectionModel().getSelectedItem();

        // Call getIdByNomDocument to fetch the corresponding id
        int documentId = documentService.getIdByNomDocument(selectedNomDocument);

        if (typeAssistance.isEmpty()) {
            showAlert("Erreur", "Type requis", Alert.AlertType.ERROR);
            return;
        }
        if (dateDemande.isEmpty()) {
            showAlert("Erreur", "Date requise", Alert.AlertType.ERROR);
            return;
        }
        if (status.isEmpty()) {
            showAlert("Erreur", "Statut requis", Alert.AlertType.ERROR);
            return;
        }

        // Pass documentId to the AssistantDocumentaire constructor
        AssistantDocumentaire newAssistant = new AssistantDocumentaire(0, 16, documentId, typeAssistance, dateDemande, status, remarque, rappelAutomatique);
        assistantService.addEntity(newAssistant);
        loadAssistantData();
        clearFields();
    }

    @FXML
    public void updateAssistant() {
        AssistantDocumentaire selectedAssistant = tableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            selectedAssistant.setTypeAssistance(txtTypeAssistance.getText().trim());
            LocalDate dateDemandeValue = dpDateDemande.getValue();
            selectedAssistant.setDateDemande((dateDemandeValue != null) ? dateDemandeValue.format(dateFormatter) : "");
            selectedAssistant.setStatus(txtStatus.getText().trim());
            selectedAssistant.setRemarque(txtRemarque.getText().trim());
            selectedAssistant.setRappelAutomatique(chkRappelAutomatique.isSelected());

            assistantService.updateEntity(selectedAssistant);
            loadAssistantData();
            clearFields();
        }
    }

    @FXML
    public void deleteAssistant() {
        AssistantDocumentaire selectedAssistant = tableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            assistantService.deleteEntity(selectedAssistant);
            loadAssistantData();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtTypeAssistance.clear();
        dpDateDemande.setValue(null);
        txtStatus.clear();
        txtRemarque.clear();
        chkRappelAutomatique.setSelected(false);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void searchButton() {
        searchField.clear();
        loadAssistantData();
        clearFields();
    }

    private void populateComboBox() {
        // Clear existing items in the ComboBox
        comboDocument.getItems().clear();

        // Retrieve all nomDocument values from the database
        List<String> nomDocuments = documentService.getAllNomDocuments();

        // Add the values to the ComboBox
        comboDocument.getItems().addAll(nomDocuments);

        // Set a default prompt text if no items are available
        if (nomDocuments.isEmpty()) {
            comboDocument.setPromptText("Aucun document disponible");
        }
    }
}
