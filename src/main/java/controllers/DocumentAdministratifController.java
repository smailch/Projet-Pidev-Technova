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
import org.json.JSONArray;
import services.DocumentAdministratifService;
import services.Mail_rec;
import services.OcrService;
import services.ProfessionalPDFGenerator;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private ComboBox<String> comboStatus;
    @FXML
    private TableColumn<DocumentAdministratif, String> colJoursDepuisEmission;
    @FXML
    private Button btnprompt;
    @FXML
    private TextArea txtareainput;
    @FXML
    private Button btnTranslateRemarque;
    @FXML
    private TextArea txtareainputchat;
    @FXML
    private Button btnprompt1;
    @FXML
    private TextArea textllmreponse;

    @FXML
    public void initialize() {
        comboStatus.getItems().addAll("Traité", "En cours", "Rejeté");

        // Load data from service and set table items
        loadDocumentData();

        // Set up search event
        searchField.setOnKeyReleased(this::handleSearch);

        // Listen for table selection changes to fill in the form fields
        tableDocuments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNomDocument.setText(newSelection.getNomDocument());
                txtCheminFichier.setText(newSelection.getCheminFichier());
                comboStatus.setValue(newSelection.getStatus());
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
        colJoursDepuisEmission.setCellValueFactory(cellData -> {
            LocalDate dateEmission = cellData.getValue().getDateEmission();
            long jours = calculerJoursDepuisEmission(dateEmission);
            return new SimpleStringProperty(jours + " jours");
        });
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
    private long calculerJoursDepuisEmission(LocalDate dateEmission) {
        if (dateEmission == null) {
            return -1; // Valeur par défaut si la date est nulle
        }
        return ChronoUnit.DAYS.between(dateEmission, LocalDate.now());
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
        String status = comboStatus.getValue();
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
        String email = "helmi.dridi@esprit.tn";

        String cn = "Votre document a ete bien enregistrer";

        String sb = "Confirmation d'Ajout document";
       Mail_rec.sendMail(email, sb, cn);
    }

    @FXML
    public void updateDocument() {
        DocumentAdministratif selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            String nomDocument = txtNomDocument.getText();
            String cheminFichier = txtCheminFichier.getText();
            String status = comboStatus.getValue();
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
        comboStatus.getSelectionModel().clearSelection();
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
                comboStatus.setValue(status);
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


    @FXML
    public void generateprompt(ActionEvent actionEvent) {
        String userInput = txtareainput.getText(); // Get user input from TextArea

        if (userInput.isEmpty()) {
            showonAlert("Erreur", "Veuillez entrer du texte avant de générer le PDF.", Alert.AlertType.ERROR);
            return;
        }

        // Generate PDF
        String pdfFilePath = ProfessionalPDFGenerator.generatePdf(userInput);

        if (pdfFilePath != null) {
            // Send email
            Mail_rec.sendEmailWithPdf("siwar.slimi@esprit.tn", pdfFilePath);
            showonAlert("Succès", "PDF généré et envoyé avec succès!", Alert.AlertType.INFORMATION);
        } else {
            showonAlert("Erreur", "La génération du PDF a échoué.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void translateRemarqueToEnglish(ActionEvent actionEvent)  {
        try {
            // Step 1: Collect all "remarque" values from the TableView
            System.out.println("Collecting all remarks from TableView...");
            List<String> remarksList = new ArrayList<>();
            for (DocumentAdministratif doc : tableDocuments.getItems()) {
                remarksList.add(doc.getRemarque());
            }

            if (remarksList.isEmpty()) {
                System.out.println("No remarks found in the TableView.");
                showonAlert("Information", "Aucune remarque à traduire.", Alert.AlertType.INFORMATION);
                return;
            }

            System.out.println("Collected " + remarksList.size() + " remarks for translation.");

            // Step 2: Convert list to JSON string
            JSONArray jsonArray = new JSONArray(remarksList);
            String jsonInput = jsonArray.toString();
            // Ensure proper escaping when passing as a command-line argument
            jsonInput = jsonInput.replace("\"", "\\\"");

// Print JSON to debug
            System.out.println("JSON to send: " + jsonInput);
            System.out.println(" Converted remarks to JSON: " + jsonInput);

            // Step 3: Call Python script
            System.out.println(" Launching Python script for translation...");

            // Print the exact command being executed
            List<String> command = new ArrayList<>();
            command.add("C:/Users/USER/Documents/gestion_paperrases_siwar_slimi/python311_script/.venv/Scripts/python.exe");
            command.add("C:/Users/USER/Documents/gestion_paperrases_siwar_slimi/python311_script/traduction_siwar.py");
            command.add(jsonInput);

            System.out.println("🖥 Running command: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Step 4: Read output from Python script
            System.out.println("⏳ Waiting for Python script to complete...");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(" Python Output: " + line);
                if (line.trim().startsWith("{") || line.trim().startsWith("[")) {
                    output.append(line.trim()); // Only store JSON output
                }
            }

            // Check if output is valid JSON
            if (!output.toString().startsWith("[")) {
                System.err.println(" Invalid JSON received from Python: " + output.toString());
                showonAlert("Erreur", "Le script Python a renvoyé une réponse invalide.", Alert.AlertType.ERROR);
                return;
            }

            // Step 5: Parse JSON response
            System.out.println(" Parsing translated JSON response...");
            JSONArray translatedArray = new JSONArray(output.toString());

            // Step 6: Update TableView with translated remarks
            System.out.println(" Updating TableView with translated remarks...");
            for (int i = 0; i < tableDocuments.getItems().size(); i++) {
                tableDocuments.getItems().get(i).setRemarque(translatedArray.getString(i));
                System.out.println("✅ Updated Row " + (i + 1) + ": " + translatedArray.getString(i));
            }

            tableDocuments.refresh();
            System.out.println(" Translation completed successfully!");
            showonAlert("Succès", "Traduction terminée avec succès !", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            System.err.println("❌ Error occurred during translation: " + e.getMessage());
            e.printStackTrace();
            showonAlert("Erreur", "Impossible de traduire les remarques.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void sendtollm(ActionEvent actionEvent) {
        String scriptPath = "C:/Users/USER/Documents/gestion_paperrases_siwar_slimi/python311_script/Test.py"; // Chemin vers le script Python
        StringBuilder output = new StringBuilder();

        try {
            // Print the exact command being executed
            List<String> command = new ArrayList<>();
            command.add("C:/Users/USER/Documents/gestion_paperrases_siwar_slimi/python311_script/.venv/Scripts/python.exe");
            command.add("C:/Users/USER/Documents/gestion_paperrases_siwar_slimi/python311_script/Test.py");
            command.add( txtareainputchat.getText());

            System.out.println("🖥 Running command: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            // Lire la sortie du script Python
            StringBuilder response = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore les lignes contenant des warnings et affiche uniquement la réponse utile
                if (!line.contains("DeprecationWarning") && !line.contains("FutureWarning") && !line.contains("warn_deprecated")) {
                    response.append(line).append("\n");
                }

            }
            process.waitFor();
            reader.close();

            // Afficher uniquement la réponse finale (sans warnings)
            System.out.println("Réponse filtrée :\n" + response.toString().trim());
            textllmreponse.setText(response.toString().trim());


        } catch (Exception e) {

            showAlert("Erreur", e.getMessage());
        }


    }
}
