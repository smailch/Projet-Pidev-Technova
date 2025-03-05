package controllers;

import entities.Incident;
import entities.ServiceIntervention;
import services.ServiceInterventionService;
import com.itextpdf.layout.property.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServiceInterventionController {

    @FXML
    private TextField nomServiceField;
    @FXML
    private ComboBox<String> typeInterventionComboBox;
    @FXML
    private TextField zoneInterventionField;
    @FXML
    private TableView<ServiceIntervention> serviceTable;
    @FXML
    private TableColumn<ServiceIntervention, String> nomColumn;
    @FXML
    private TableColumn<ServiceIntervention, String> typeColumn;
    @FXML
    private TableColumn<ServiceIntervention, String> zoneColumn;

    private final ServiceInterventionService serviceInterventionService = new ServiceInterventionService();
    private ObservableList<ServiceIntervention> serviceList = FXCollections.observableArrayList();

    private static final String[] TYPE_INTERVENTION_VALUES = {"VOIRIE", "ECLAIRAGE", "PROPRETE", "AUTRE"};

    @FXML
    private void initialize() {
        typeInterventionComboBox.getItems().addAll(TYPE_INTERVENTION_VALUES);
        serviceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // remplirChampsService(newSelection); // Vérifie si ce comportement pose problème
            }
        });
        // Liaison des colonnes aux propriétés de ServiceIntervention
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomServiceProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeInterventionProperty());
        zoneColumn.setCellValueFactory(cellData -> cellData.getValue().zoneInterventionProperty());

        // Chargement des services dans la table
        loadServices();
    }


    private void loadServices() {
        serviceList.clear(); // Nettoyer la liste avant d'ajouter de nouvelles données
        serviceList.addAll(serviceInterventionService.getAllservices()); // Ajouter les nouveaux services
        serviceTable.setItems(serviceList); // Mettre à jour la TableView
    }
    @FXML
    private void ajouterService() throws SQLException {
        String nomService = nomServiceField.getText();
        String typeIntervention = typeInterventionComboBox.getValue();
        String zoneIntervention = zoneInterventionField.getText();

        if (nomService.isEmpty() || typeIntervention == null || zoneIntervention.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        ServiceIntervention service = new ServiceIntervention(0, nomService, typeIntervention, zoneIntervention);
        if (serviceInterventionService.ajouterService(service)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service ajouté avec succès !");
            loadServices();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du service.");
        }
    }

    @FXML

    private void modifierService() {
        ServiceIntervention selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (selectedService == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un service à modifier.");
            return;
        }

        // Vérification des champs vides
        if (nomServiceField.getText().isEmpty() ||
                typeInterventionComboBox.getValue() == null ||
                zoneInterventionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez remplir tous les champs.");
            return;
        }

        // Mise à jour des valeurs
        selectedService.setNomService(nomServiceField.getText());
        selectedService.setTypeIntervention(typeInterventionComboBox.getValue());
        selectedService.setZoneIntervention(zoneInterventionField.getText());

        // Mise à jour en base de données
        if (serviceInterventionService.modifierService(selectedService)) {
            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service modifié avec succès !");

            // Rafraîchir la TableView
            serviceTable.refresh();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service modifié avec succès !");
        }

    }
    private void remplirChampsService(ServiceIntervention service) {
        if (service != null) {
            nomServiceField.setText(service.getNomService());
            typeInterventionComboBox.setValue(service.getTypeIntervention());
            zoneInterventionField.setText(service.getZoneIntervention());
        }
    }
    @FXML
    private void supprimerService() {
        ServiceIntervention selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un service à supprimer.");
            return;
        }

        if (serviceInterventionService.supprimerService(selectedService.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Service supprimé avec succès !");
            loadServices();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "il ya un ou plusier incidents ont  affectent a cette servise.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void goToadminIncidentPage(javafx.event.ActionEvent event) {
        loadPage(event, "/adminincidentForm.fxml");
    }
    @FXML
    private void goToexportPage(javafx.event.ActionEvent event) {
        loadPage(event, "/serviceinterventionexport.fxml");
    }
    @FXML
    private void goToadminservicePage(javafx.event.ActionEvent event) {
        loadPage(event, "/serviceintervention.fxml");
    }
    @FXML
    private void goToadminhistoriquePage(javafx.event.ActionEvent event) {
        loadPage(event, "/historique.fxml");
    }
    private void loadPage(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Obtenir la scène actuelle et remplacer le contenu
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
    @FXML
    private void exportToPDF(ActionEvent event) {
        ServiceIntervention selectedService = serviceTable.getSelectionModel().getSelectedItem();

        // Vérifier si aucun service n'est sélectionné
        if (selectedService == null) {
            showAlert("Aucun service sélectionné", "Veuillez sélectionner un service avant d'exporter.");
            return;
        }

        // Récupérer la liste des incidents pour le service sélectionné
        List<Incident> incidents = serviceInterventionService.getIncidentsByService(selectedService.getId());

        // Vérifier si aucun incident n'est trouvé
        if (incidents.isEmpty()) {
            showAlert("Aucun incident", "Aucun incident trouvé pour ce service.");
            return;
        }

        // Sélection du fichier de destination via FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("Incidents_" + selectedService.getNomService() + ".pdf");
        File file = fileChooser.showSaveDialog(null);

        // Si un fichier est sélectionné
        if (file != null) {
            try {
                // Créer un PdfWriter et un PdfDocument
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Titre du document, centré et en gras
                document.add(new Paragraph("Rapport des Incidents du Service : " + selectedService.getNomService())
                        .setFontSize(18)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20));

                // Créer un tableau avec 5 colonnes (ajout d'une colonne vide pour l'employé)
                Table table = new Table(5);

                // Ajouter un titre pour la colonne vide
               // Titre de la colonne vide
                table.addCell("ID");
                table.addCell("Description");
                table.addCell("Localisation");
                table.addCell("Date");
                table.addCell("valide par l'employer");

                // Ajouter chaque incident au tableau
                for (Incident incident : incidents) {
                    // Colonne vide pour que l'employé puisse remplir manuellement les informations complémentaires
                    table.addCell(String.valueOf(incident.getId()));
                    table.addCell(incident.getDescription());
                    table.addCell(incident.getLocalisation());

                    // Formater la date pour l'afficher dans un format lisible
                    String formattedDate = (incident.getDateSignalement() != null)
                            ? new SimpleDateFormat("dd/MM/yyyy").format(incident.getDateSignalement())
                            : "Date non disponible";
                    table.addCell(formattedDate);
                    table.addCell("");
                }

                // Ajouter le tableau au document PDF
                document.add(table);

                // Fermer le document
                document.close();

                // Afficher une alerte de succès
                showAlert("Succès", "Le PDF a été généré avec succès !");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de créer le fichier PDF.");
            }
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
