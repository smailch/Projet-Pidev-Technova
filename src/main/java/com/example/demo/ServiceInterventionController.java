package com.example.demo;

import com.example.demo.entities.ServiceIntervention;
import com.example.demo.services.ServiceInterventionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

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
    private void ajouterService() {
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
    private void goToadminservicePage(javafx.event.ActionEvent event) {
        loadPage(event, "/serviceintervention.fxml");
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
}
