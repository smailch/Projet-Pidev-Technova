package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import entities.DocumentAdministratif;
import services.DocumentAdministratifService;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DocumentAdministratifController {

    @FXML
    private TableView<DocumentAdministratif> tableDocuments;
    @FXML
    private TableColumn<DocumentAdministratif, String> colNomDocument;
    @FXML
    private TableColumn<DocumentAdministratif, String> colCheminFichier;
    @FXML
    private TableColumn<DocumentAdministratif, String> colDateEmission;
    @FXML
    private TableColumn<DocumentAdministratif, String> colStatus;
    @FXML
    private TableColumn<DocumentAdministratif, String> colRemarque;

    @FXML
    private TextField txtNomDocument;
    @FXML
    private TextField txtCheminFichier;
    @FXML
    private DatePicker datePickerDateEmission;

    @FXML
    private TextField txtStatus;
    @FXML
    private TextField txtRemarque;
    @FXML
    private TextField searchField;

    @FXML
    private Label lblNomDocumentError;
    @FXML
    private Label lblCheminFichierError;
    @FXML
    private Label lblDateEmissionError;
    @FXML
    private Label lblStatusError;
    @FXML
    private Label lblRemarqueError;

    private DocumentAdministratifService documentService = new DocumentAdministratifService();
    private ObservableList<DocumentAdministratif> allDocuments = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Load data from service and set table items
        loadDocumentData();

        // Set up search event
        searchField.setOnKeyReleased(this::handleSearch);

        // Listen for table selection changes to fill in the form fields
        tableDocuments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNomDocument.setText(newSelection.getNomDocument());
                txtCheminFichier.setText(newSelection.getCheminFichier());
                String dateDemande = newSelection.getDateEmission();
                if (dateDemande != null && !dateDemande.isEmpty()) {
                    try {
                        datePickerDateEmission.setValue(LocalDate.parse(dateDemande, dateFormatter));
                    } catch (DateTimeParseException e) {
                        datePickerDateEmission.setValue(null);
                    }
                } else {
                    datePickerDateEmission.setValue(null);
                }
                txtStatus.setText(newSelection.getStatus());
                txtRemarque.setText(newSelection.getRemarque());
                clearErrorMessages();
            }
        });

        // Set cell value factories for columns
        colNomDocument.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomDocument()));
        colCheminFichier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheminFichier()));
        colDateEmission.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateEmission()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colRemarque.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemarque()));

        // --- Custom cell factory for the file column ---
        colCheminFichier.setCellFactory(column -> new TableCell<DocumentAdministratif, String>() {
            private final ImageView imageView = new ImageView();
            private final Button openButton = new Button("Ouvrir");

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.trim().isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String lowerPath = path.toLowerCase();
                    if (lowerPath.endsWith(".png") || lowerPath.endsWith(".jpg") ||
                            lowerPath.endsWith(".jpeg") || lowerPath.endsWith(".gif")) {
                        try {
                            Image image = new Image(new File(path).toURI().toURL().toExternalForm(), 100, 100, true, true);
                            imageView.setImage(image);
                            setGraphic(imageView);
                            setText(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setGraphic(null);
                            setText("Erreur de chargement");
                        }
                    } else {
                        openButton.setOnAction(event -> {
                            try {
                                File file = new File(path);
                                if (file.exists()) {
                                    Desktop.getDesktop().open(file);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        setGraphic(openButton);
                        setText(null);
                    }
                }
            }
        });
    }

    private void loadDocumentData() {
        List<DocumentAdministratif> documents = documentService.getAllData();
        allDocuments.setAll(documents);
        tableDocuments.setItems(allDocuments);
    }

    @FXML
    public void handleSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase();
        filterData(query);
        clearFields();
    }

    private void filterData(String query) {
        ObservableList<DocumentAdministratif> filteredList = FXCollections.observableArrayList();
        for (DocumentAdministratif doc : allDocuments) {
            if (doc.getNomDocument().toLowerCase().contains(query) ||
                    doc.getCheminFichier().toLowerCase().contains(query) ||
                    doc.getDateEmission().toLowerCase().contains(query)) {
                filteredList.add(doc);
            }
        }
        tableDocuments.setItems(filteredList);
    }

    @FXML
    public void addDocument() {
        String nomDocument = txtNomDocument.getText();
        String cheminFichier = txtCheminFichier.getText();
        String status = txtStatus.getText();
        String remarque = txtRemarque.getText();

        // Format the date from DatePicker to string (yyyy-MM-dd)
        String dateEmission = null;
        if (datePickerDateEmission.getValue() != null) {
            dateEmission = datePickerDateEmission.getValue().format(dateFormatter);
        }

        // Validation for all fields
        if (nomDocument.isEmpty()) {
            showAlert("Nom requis", "Le nom du document est requis.");
            return;
        }
        if (nomDocument.length() > 255) { // Example check for maximum length
            showAlert("Nom trop long", "Le nom du document ne peut pas dépasser 255 caractères.");
            return;
        }

        if (cheminFichier.isEmpty()) {
            showAlert("Chemin requis", "Le chemin du fichier est requis.");
            return;
        }
        File file = new File(cheminFichier);
        if (!file.exists()) {
            showAlert("Fichier non trouvé", "Le fichier spécifié n'a pas été trouvé.");
            return;
        }

        if (dateEmission == null || dateEmission.isEmpty()) {
            showAlert("Date requise", "La date d'émission est requise.");
            return;
        }
        if (datePickerDateEmission.getValue() != null && datePickerDateEmission.getValue().isAfter(LocalDate.now())) {
            showAlert("Date future", "La date ne peut pas être future.");
            return;
        }

        if (status.isEmpty()) {
            showAlert("Statut requis", "Le statut est requis.");
            return;
        }

        DocumentAdministratif newDocument = new DocumentAdministratif(0, nomDocument, cheminFichier, dateEmission, status, remarque);
        documentService.addEntity(newDocument);

        loadDocumentData();
        clearFields();
    }

    @FXML
    public void updateDocument() {
        DocumentAdministratif selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            String nomDocument = txtNomDocument.getText();
            String cheminFichier = txtCheminFichier.getText();
            String status = txtStatus.getText();
            String remarque = txtRemarque.getText();

            String dateEmission = null;
            if (datePickerDateEmission.getValue() != null) {
                dateEmission = datePickerDateEmission.getValue().format(dateFormatter);
            }

            // Validation for all fields
            if (nomDocument.isEmpty()) {
                showAlert("Nom requis", "Le nom du document est requis.");
                return;
            }
            if (nomDocument.length() > 255) { // Example check for maximum length
                showAlert("Nom trop long", "Le nom du document ne peut pas dépasser 255 caractères.");
                return;
            }

            if (cheminFichier.isEmpty()) {
                showAlert("Chemin requis", "Le chemin du fichier est requis.");
                return;
            }
            File file = new File(cheminFichier);
            if (!file.exists()) {
                showAlert("Fichier non trouvé", "Le fichier spécifié n'a pas été trouvé.");
                return;
            }

            if (dateEmission == null || dateEmission.isEmpty()) {
                showAlert("Date requise", "La date d'émission est requise.");
                return;
            }
            if (datePickerDateEmission.getValue() != null && datePickerDateEmission.getValue().isAfter(LocalDate.now())) {
                showAlert("Date future", "La date ne peut pas être future.");
                return;
            }

            if (status.isEmpty()) {
                showAlert("Statut requis", "Le statut est requis.");
                return;
            }

            selectedDocument.setNomDocument(nomDocument);
            selectedDocument.setCheminFichier(cheminFichier);
            selectedDocument.setDateEmission(dateEmission);
            selectedDocument.setStatus(status);
            selectedDocument.setRemarque(remarque);

            documentService.updateEntity(selectedDocument);
            loadDocumentData();
            clearFields();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    public void deleteDocument() {
        DocumentAdministratif selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            documentService.deleteEntity(selectedDocument);
            loadDocumentData();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtNomDocument.clear();
        txtCheminFichier.clear();
        datePickerDateEmission.setValue(null);
        txtStatus.clear();
        txtRemarque.clear();
        clearErrorMessages();
    }

    private void clearErrorMessages() {
        if (lblNomDocumentError != null) {
            lblNomDocumentError.setText("");
            lblNomDocumentError.setVisible(false);
        }
        if (lblCheminFichierError != null) {
            lblCheminFichierError.setText("");
            lblCheminFichierError.setVisible(false);
        }
        if (lblDateEmissionError != null) {
            lblDateEmissionError.setText("");
            lblDateEmissionError.setVisible(false);
        }
        if (lblStatusError != null) {
            lblStatusError.setText("");
            lblStatusError.setVisible(false);
        }
        if (lblRemarqueError != null) {
            lblRemarqueError.setText("");
            lblRemarqueError.setVisible(false);
        }
    }


    @FXML
    public void searchButton() {
        searchField.clear();
        loadDocumentData();
        clearFields();
    }

    @FXML
    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez un fichier");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        Window stage = txtCheminFichier.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            txtCheminFichier.setText(selectedFile.getAbsolutePath());
        }
    }
}
