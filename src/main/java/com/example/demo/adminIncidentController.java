package com.example.demo;

import com.example.demo.entities.Incident;
import com.example.demo.services.IncidentService;
import com.example.demo.tools.MyConnection;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class adminIncidentController {

    private StringProperty dynamicLocalisation = new SimpleStringProperty("");

    // Votre label déclaré dans le FXML
    @FXML
    private Label localisationLabel;
    @FXML
    private TextField descriptionField;
    @FXML

    private ComboBox<String> statutComboBox;
    @FXML
    private TextField serviceAffecteField;

    @FXML
    private TableView<Incident> incidentTable;
    @FXML
    private TableColumn<Incident, String> colTypeIncident;
    @FXML
    private TableColumn<Incident, String> colDescription;
    @FXML
    private TableColumn<Incident, String> colLocalisation;
    @FXML
    private TableColumn<Incident, String> colStatut;
    @FXML
    private TableColumn<Incident, String> colServiceAffecte;
    @FXML
    private TableColumn<Incident, String> colImage;

    @FXML
    private ComboBox<String> typeIncidentComboBox;
    @FXML
    private ComboBox<String> typeIncidentField;
    // WebView pour la carte et pour l'autocomplétion
    @FXML
    private WebView mapView;
    @FXML
    private WebView autocompleteView;
    private boolean isMapVisible = false;
    @FXML

    private Text affichemapText;
    @FXML
    private ComboBox<String> comboServices;
    @FXML
    private Button btnAffecter;
    // Instance de IncidentService
    private final IncidentService incidentService = new IncidentService();
    private ObservableList<Incident> incidentsList;

    private static final String[] STATUT_VALUES = {"En attente", "En cours", "Résolu"};
    private static final String[] TYPE_INCIDENT_VALUES = {"Voirie", "Éclairage", "Déchet", "Autre"};

    private double latitude;  // Variables pour stocker les coordonnées
    private double longitude;
    @FXML
    private TextField searchLocationField;

    @FXML
    private ListView<String> locationListView;

    @FXML
    private ImageView imageView; // Ajouter un ImageView pour prévisualiser l'image

    private String imagePath; // Stocker le chemin de l'image sélectionnée
    @FXML
    private Button confirmAddressButton;

    // Méthode pour gérer l'appui sur la touche "Entrée"
    @FXML
    public void onEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            updateIncident();
        }

    }

    private void updateIncident() {
        String typeIncident = typeIncidentField.getValue();
        String description = descriptionField.getText();
        String localisation = localisationLabel.getText();
        String statut = statutComboBox.getValue();


        Incident incident = new Incident(typeIncident, description, localisation, statut, latitude, longitude, imagePath);
        // Ajouter à la base de données
        incidentService.ajouterIncident(incident);

        incidentTable.getItems().add(incident);
        clearFields();
    }

    private void clearFields() {


        statutComboBox.getSelectionModel().clearSelection(); // Réinitialiser la sélection
        statutComboBox.setValue(null); // Remettre le champ à vide


    }

    @FXML
    private void initialize() {
        // Liaison du label à la propriété dynamicLocalisation




        // Initialisation des colonnes du tableau
        colTypeIncident.setCellValueFactory(cellData -> cellData.getValue().typeIncidentProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colLocalisation.setCellValueFactory(cellData -> cellData.getValue().localisationProperty());
        colStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
        colServiceAffecte.setCellValueFactory(cellData -> cellData.getValue().serviceAffecteProperty().asObject().asString());
        List<String> serviceNames = getAllServiceNames();
        colImage.setCellValueFactory(cellData -> cellData.getValue().imagePathProperty());


        // Set up image column
        colImage.setCellFactory(column -> new TableCell<Incident, String>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(imagePath));
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        });
        comboServices.setItems(FXCollections.observableArrayList(incidentService.getAllServiceNames()));
        statutComboBox.getItems().addAll(STATUT_VALUES);




        colServiceAffecte.setCellValueFactory(cellData -> {
            int serviceId = cellData.getValue().getServiceAffecte(); // Supposons que tu as une méthode `getServiceAffecte()` dans ton modèle `Incident`
            String serviceName = getServiceNameById(serviceId);
            return new SimpleStringProperty(serviceName);
        });
        // Initialiser la carte et l'autocomplétion






        chargerIncidents();
        incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                incidentService.afficherIncidents() ;
            }
        });
    }

    public String getServiceNameById(int serviceId) {
        String serviceName = null;
        String req = "SELECT nom_service FROM serviceintervention WHERE id = ?";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    serviceName = rs.getString("nom_service");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nom du service : " + e.getMessage());
        }

        return serviceName;
    }



    private void chargerIncidents() {
        incidentsList = FXCollections.observableArrayList(incidentService.getAllIncidents());
        incidentTable.setItems(incidentsList);

    }







    private void showSuccessAlert(String title, String message) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(title);
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void modifierstatusIncident() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }


        String statut = statutComboBox.getValue();



        if (statut == null || statut.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }



        // Mise à jour de l'incident sélectionné

        selectedIncident.setStatut(statut);


        // Mise à jour de l'incident dans la base de données
        incidentService.modifierStatusIncident(selectedIncident);
        chargerIncidents();
        clearFields();
        showSuccessAlert("Succès", "Incident modifié avec succès !");
    }

    @FXML
    private void modifierIncident() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }

        String typeIncident = typeIncidentComboBox.getValue();
        String description = descriptionField.getText();
        String statut = statutComboBox.getValue();
        String localisation = dynamicLocalisation.get();

        // Vérification des champs obligatoires
        if (typeIncident == null || typeIncident.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un type d'incident.");
            return;
        }

        if (description == null || description.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une description.");
            return;
        }

        if (localisation == null || localisation.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une localisation.");
            return;
        }

        if (statut == null || statut.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }



        // Mise à jour de l'incident sélectionné
        selectedIncident.setTypeIncident(typeIncident);
        selectedIncident.setDescription(description);
        selectedIncident.setLocalisation(localisation);
        selectedIncident.setStatut(statut);
        selectedIncident.setLatitude(latitude);
        selectedIncident.setLongitude(longitude);

        // Mise à jour de l'incident dans la base de données
        incidentService.modifierIncident(selectedIncident);
        chargerIncidents();
        clearFields();
        showSuccessAlert("Succès", "Incident modifié avec succès !");
    }
    private void remplirChampsIncident(Incident incident) {
        typeIncidentComboBox.setValue(incident.getTypeIncident());
        descriptionField.setText(incident.getDescription());
        dynamicLocalisation.set(incident.getLocalisation());
        statutComboBox.setValue(incident.getStatut());


        // Mettre à jour les coordonnées
        latitude = incident.getLatitude();
        longitude = incident.getLongitude();
    }
    @FXML
    private void supprimerIncident() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();
        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }
        incidentService.supprimerIncident(selectedIncident);
        chargerIncidents();
        clearFields();
        showSuccessAlert("Succès", "Incident supprimé avec succès !");
    }







    public List<String> getAllServiceNames() {
        List<String> serviceNames = new ArrayList<>();
        String req = "SELECT nom FROM service";

        try (PreparedStatement stmt = MyConnection.getConnection().prepareStatement(req);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                serviceNames.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des services : " + e.getMessage());
        }

        return serviceNames;
    }
    @FXML
    private void affecterService() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();
        String selectedService = comboServices.getSelectionModel().getSelectedItem();

        if (selectedIncident != null && selectedService != null) {
            boolean success = incidentService.affecterServiceParNom(selectedIncident.getId(), selectedService);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service affecté avec succès !");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Échec de l'affectation.");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un incident et un service.");
            alert.show();
        }
        chargerIncidents();
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