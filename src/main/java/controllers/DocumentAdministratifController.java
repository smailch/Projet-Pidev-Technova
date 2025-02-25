package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import entities.DocumentAdministratif;
import services.DocumentAdministratifService;
import services.OcrService;

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
    private TextField txtStatus;
    @FXML
    private TextField txtRemarque;
    @FXML
    private TextField searchField;

    @FXML
    private Label lblNomDocumentError;
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
    private SideBarController nullController;
    @FXML
    private Button btnOcrExtract;

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
                txtStatus.setText(newSelection.getStatus());
                txtRemarque.setText(newSelection.getRemarque());
                clearErrorMessages();
            }
        });

        // Set cell value factories for columns
        colNomDocument.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomDocument()));
        colCheminFichier.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheminFichier()));
        colDateEmission.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateEmission().toString()));
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
                    doc.getDateEmission().toString().toLowerCase().contains(query)) {
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


        if (status.isEmpty()) {
            showAlert("Statut requis", "Le statut est requis.");
            return;
        }
        // --------------------------------------------
        List<DocumentAdministratif> allDocs = documentService.getAllData();
        for (DocumentAdministratif existing : allDocs) {
            if (existing.getNomDocument().equalsIgnoreCase(nomDocument)) {
                showErrorAlert("Erreur", "Un document avec ce nom existe déjà!");
                return; // Stop creation if duplicate
            }
        }
        DocumentAdministratif newDocument = new DocumentAdministratif(0, nomDocument, cheminFichier, status, remarque);
        documentService.addEntity(newDocument);

        loadDocumentData();
        clearFields();

        // Show success alert
        showInfoAlert("Succès", "Le document a été ajouté avec succès !");
    }

    @FXML
    public void updateDocument() {
        DocumentAdministratif selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            String nomDocument = txtNomDocument.getText();
            String cheminFichier = txtCheminFichier.getText();
            String status = txtStatus.getText();
            String remarque = txtRemarque.getText();



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


            if (status.isEmpty()) {
                showErrorAlert("Statut requis", "Le statut est requis.");
                return;
            }

            selectedDocument.setNomDocument(nomDocument);
            selectedDocument.setCheminFichier(cheminFichier);
            selectedDocument.setStatus(status);
            selectedDocument.setRemarque(remarque);

            documentService.updateEntity(selectedDocument);
            loadDocumentData();
            clearFields();

            // Show success alert
            showInfoAlert("Succès", "Le document a été modifié avec succès !");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showonAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
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

            // Show success alert
            showInfoAlert("Succès", "Le document a été supprimé avec succès !");
        }
    }

    @FXML
    private void clearFields() {
        txtNomDocument.clear();
        txtCheminFichier.clear();
        txtStatus.clear();
        txtRemarque.clear();
        clearErrorMessages();
    }

    private void clearErrorMessages() {
        if (lblNomDocumentError != null) {
            lblNomDocumentError.setText("");
            lblNomDocumentError.setVisible(false);
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

    // For error messages
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // For success/info messages
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    public void next_entity(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/AssistantDocumentaire.fxml", currentStage, titleBar);
    }

    @FXML
    public void PDFSELECTOR(ActionEvent actionEvent) {
        DocumentAdministratif selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            documentService.ExportPDF(selectedDocument);
            loadDocumentData();
            clearFields();

            // Show success alert
            showInfoAlert("Succès", "Le document a été extracter !");
        }
    }

    @FXML
    public void handleOcrExtraction(ActionEvent actionEvent) {

        // 1) Let the user pick an image file (photo or scanned doc).
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez une image du document");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.tif", "*.tiff")
        );
        Window stage = btnOcrExtract.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // 2) Perform OCR on the chosen file (Tess4J, or external API).
                String extractedText = OcrService.performOcr(selectedFile);

                // 3) Parse the text for relevant fields
                //    Adjust these labels to match your PDF EXACTLY:
                String typeAssistance = parseTypeAssistance(extractedText);  // from "Dossier No:"
                String status          = parseStatus(extractedText);         // from "Status du dossier"
                String remarque        = parseRemarque(extractedText);       // from "Moyen du paiment"
                System.out.println(status);
                System.out.println("ena houni");
                System.out.println(remarque);
                // 4) Fill the form fields
                txtCheminFichier.setText(selectedFile.toString());
                txtNomDocument.setText(typeAssistance);
                txtStatus.setText(status);
                txtRemarque.setText(remarque);

                // Optional: show the raw text to confirm
                showonAlert("OCR Extraction Réussie",
                        "Texte détecté:\n\n" + extractedText,
                        Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                e.printStackTrace();
                showonAlert("Erreur OCR",
                        "Impossible d’extraire le texte depuis l’image.",
                        Alert.AlertType.ERROR);
            }
        }

    }
    // --------------------------------------------
    // Updated parse methods to match your PDF
    // --------------------------------------------
    private String parseTypeAssistance(String text) {
        // e.g. "Dossier No: 1"
        String label = "Nom de document";
        return extractAfterLabel(text, label);
    }



    private String parseStatus(String text) {
        // e.g. "Status du dossier" on one line, and "En cours" on the next line
        String label = "Status du document";
        return extractAfterLabel(text, label);
    }

    private String parseRemarque(String text) {
        // e.g. "Moyen du paiment" then "Carte bancaire" on next line
        // If your PDF spells it "paiement," be sure to match that exact string
        String label = "Remarque";
        return extractAfterLabel(text, label);
    }
    private String extractNextLineAfterLabel(String fullText, String label) {
        // 1) Find where "Status du dossier" (or any label) appears.
        int labelIndex = fullText.indexOf(label);
        if (labelIndex == -1) {
            return ""; // label not found
        }

        // 2) Find the end of that line (the newline character).
        int labelLineEnd = fullText.indexOf("\n", labelIndex);
        if (labelLineEnd == -1) {
            return ""; // no newline found; might be end of text
        }

        // 3) The "next line" starts right after that newline.
        int nextLineStart = labelLineEnd + 1;

        // 4) Find where the next line ends (the next newline or end of text).
        int nextLineEnd = fullText.indexOf("\n", nextLineStart);
        if (nextLineEnd == -1) {
            nextLineEnd = fullText.length();
        }

        // 5) Extract the substring of that "next line" and trim.
        return fullText.substring(nextLineStart, nextLineEnd).trim();
    }


    /**
     * Extracts the substring after label until the next newline (or end of text).
     */
    private String extractAfterLabel(String fullText, String label) {
        int idx = fullText.indexOf(label);
        if (idx != -1) {
            int start = idx + label.length();
            int end = fullText.indexOf("\n", start);
            if (end == -1) end = fullText.length();
            return fullText.substring(start, end).trim();
        }
        return "";
    }


}
