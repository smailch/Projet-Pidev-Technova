package controllers;

import entities.AssistantDocumentaire;
import entities.DocumentAdministratif;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.AssistantDocumentaireService;
import services.DocumentAdministratifService;
import services.SessionManager;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Label lblTypeAssistanceError;
    @FXML
    private VBox rightPanel;
    @FXML
    private Label lblStatusError;
    @FXML
    private SideBarController nullController;
    @FXML
    private Label lblDateDemandeError;
    @FXML
    private Label lblRemarqueError;
    @FXML
    private Label lblDocumentError;
    @FXML
    private Button btnOcrExtract;

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
        // Check for duplicates (based on typeAssistance).
        List<AssistantDocumentaire> allAssistantsInDb = assistantService.getAllData();
        for (AssistantDocumentaire existing : allAssistantsInDb) {
            if (existing.getTypeAssistance().equalsIgnoreCase(typeAssistance)) {
                showAlert("Erreur", "Un assistant avec ce type existe déjà!", Alert.AlertType.ERROR);
                return; // Stop creation if duplicate found
            }
        }
        // Pass documentId to the AssistantDocumentaire constructor
        AssistantDocumentaire newAssistant = new AssistantDocumentaire(0, 16, documentId, typeAssistance, dateDemande, status, remarque, rappelAutomatique);
        assistantService.addEntity(newAssistant);
        loadAssistantData();
        clearFields();
        // Show success alert
        showAlert("Succès", "L'assistant documentaire a été créé avec succès !", Alert.AlertType.INFORMATION);

    }

    @FXML
    public void updateAssistant() {
        AssistantDocumentaire selectedAssistant = tableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            // Validate Type d'Assistance
            String typeAssistance = txtTypeAssistance.getText().trim();
            if (typeAssistance.isEmpty()) {
                showAlert("Type d'assistance requis", "Le type d'assistance est requis.",Alert.AlertType.ERROR);
                return;
            }
            if (typeAssistance.length() > 255) {
                showAlert("Type trop long", "Le type d'assistance ne peut pas dépasser 255 caractères.",Alert.AlertType.ERROR);
                return;
            }

            // Validate Date de Demande
            String dateDemande = "";
            if (dpDateDemande.getValue() != null) {
                // Convert to LocalDateTime and then to Timestamp
                LocalDateTime localDateTime = dpDateDemande.getValue().atStartOfDay();  // start of the day
                dateDemande = Timestamp.valueOf(localDateTime).toString();  // Converts to correct format
                if (dpDateDemande.getValue().isAfter(LocalDate.now())) {
                    showAlert("Date future", "La date de demande ne peut pas être dans le futur.", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                showAlert("Date requise", "La date de demande est requise.", Alert.AlertType.ERROR);
                return;
            }


            // Validate Statut
            String status = txtStatus.getText().trim();
            if (status.isEmpty()) {
                showAlert("Statut requis", "Le statut est requis.", Alert.AlertType.ERROR);
                return;
            }

            // Validate Remarque (optional, but length limit)
            String remarque = txtRemarque.getText().trim();
            if (remarque.length() > 500) {
                showAlert("Remarque trop longue", "La remarque ne peut pas dépasser 500 caractères.",Alert.AlertType.ERROR);
                return;
            }

            // Validate Rappel Automatique (checkbox is boolean, no need for extra validation)
            boolean rappelAutomatique = chkRappelAutomatique.isSelected();

            // Set validated values to the object
            selectedAssistant.setTypeAssistance(typeAssistance);
            selectedAssistant.setDateDemande(dateDemande);
            selectedAssistant.setStatus(status);
            selectedAssistant.setRemarque(remarque);
            selectedAssistant.setRappelAutomatique(rappelAutomatique);

            // Update entity
            assistantService.updateEntity(selectedAssistant);
            loadAssistantData();
            clearFields();
            // Show success alert
            showAlert("Succès", "L'assistant documentaire a été mis à jour avec succès !", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un assistant à mettre à jour.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void deleteAssistant() {
        AssistantDocumentaire selectedAssistant = tableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            assistantService.deleteEntity(selectedAssistant);
            loadAssistantData();
            clearFields();

            // Show success alert
            showAlert("Succès", "L'assistant documentaire a été supprimé avec succès !", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un assistant à supprimer.", Alert.AlertType.ERROR);
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

    @FXML
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

    @FXML
    public void retour(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DocumentAdministratif.fxml", currentStage, titleBar);
    }

}

