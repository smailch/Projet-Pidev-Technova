package com.example.demo;

import com.example.demo.entities.Incident;
import com.example.demo.services.GoogleMapsService;
import com.example.demo.services.IncidentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IncidentController {

    private StringProperty dynamicLocalisation = new SimpleStringProperty("");

    // Votre label déclaré dans le FXML
    @FXML
    private Label localisationLabel;
    @FXML
    private TextField descriptionField;

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
    private TableColumn<Incident, String> colImage;
    @FXML
    private TableColumn<Incident, String> colServiceAffecte;
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


        Incident incident = new Incident(typeIncident, description, localisation, latitude, longitude, imagePath);
        // Ajouter à la base de données
        incidentService.ajouterIncident(incident);

        incidentTable.getItems().add(incident);
        clearFields();
    }

    private void clearFields() {
        typeIncidentComboBox.getSelectionModel().clearSelection(); // Réinitialiser la sélection
        typeIncidentComboBox.setValue(null); // Remettre le champ à vide

        descriptionField.clear(); // Effacer le champ de description
        dynamicLocalisation.set(""); // Réinitialiser la localisation dynamique



        imagePath = null; // Réinitialiser le chemin de l'image
        imageView.setImage(null); // Supprimer l'image affichée
    }
    @FXML
    private void initialize() {
        localisationLabel.textProperty().bind(dynamicLocalisation);

        typeIncidentComboBox.getItems().addAll(TYPE_INCIDENT_VALUES);
        imageView.getImage();
        imagePath = null;
        initMapView();
        initAutocomplete();
        mapView.setVisible(false);
        confirmAddressButton.setVisible(false);
        if (incidentTable != null) {
            // Populate the table with incidents


            incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    remplirChampsIncident(newSelection); // Remplir les champs avec les données de l'incident sélectionné
                }
            });
            // Initialize table columns
            colTypeIncident.setCellValueFactory(cellData -> cellData.getValue().typeIncidentProperty());
            colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
            colLocalisation.setCellValueFactory(cellData -> cellData.getValue().localisationProperty());
            colStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
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
            initMapView();
            mapView.setVisible(false);
            confirmAddressButton.setVisible(false);



            chargerIncidents();
            incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    incidentService.afficherIncidents();
                }
            });
        }
    }


    // Initialisation du WebView pour la carte Google Maps

    private void initMapView() {
        String mapScript = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
            <meta charset="utf-8">
            <style>
                #map {
                    height: 100vh;
                    width: 100%;
                }
                html, body {
                    height: 100%;
                    margin: 0;
                    padding: 0;
                }
            </style>
            <script>
                function loadGoogleMaps() {
                    const script = document.createElement("script");
                    script.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyDFUn6EuiuNTZ0TsETQ-BhCpmMcvOA7FME&callback=initMap";
                    script.async = true;
                    script.defer = true;
                    document.head.appendChild(script);
                }

                function initMap() {
                    const map = new google.maps.Map(document.getElementById("map"), {
                        center: { lat: 36.8065, lng: 10.1815 },
                        zoom: 13,
                        mapTypeId: "roadmap"
                    });

                    const marker = new google.maps.Marker({
                        position: { lat: 36.8065, lng: 10.1815 },
                        map,
                        draggable: true
                    });

                    marker.addListener("dragend", (event) => {
                        const lat = event.latLng.lat();
                        const lng = event.latLng.lng();
                        document.getElementById("coordinates").innerText = lat + "," + lng;
                    });
                }

                window.onload = loadGoogleMaps;
            </script>
        </head>
        <body>
            <div id="map"></div>
            <div id="coordinates" style="display:none;">36.8065,10.1815</div>
        </body>
        
        </html>
        """;

        WebEngine webEngine = mapView.getEngine();
        webEngine.loadContent(mapScript);
    }

    @FXML
    private boolean showMap() {
        if (!isMapVisible) {
            mapView.setVisible(true);
            isMapVisible = true;
            confirmAddressButton.setVisible(true);         }
        return isMapVisible;
    }




    @FXML
    private void fetchAndSetAddress() {
        WebEngine webEngine = mapView.getEngine();
        // Récupérer le contenu de l'élément "coordinates" mis à jour par le drag end du marqueur
        String coords = (String) webEngine.executeScript("document.getElementById('coordinates').innerText");
        if (coords != null && coords.contains(",")) {
            String[] parts = coords.split(",");
            try {
                latitude = Double.parseDouble(parts[0].trim());
                longitude = Double.parseDouble(parts[1].trim());
                // Appeler votre service pour obtenir l'adresse correspondante aux coordonnées
                String address = GoogleMapsService.fetchAddress(latitude, longitude);
                // Mettre à jour la propriété liée au Label
                dynamicLocalisation.set(address);
                System.out.println("Adresse obtenue : " + address);
            } catch (NumberFormatException e) {
                System.out.println("Erreur lors de la conversion des coordonnées.");
            }
        } else {
            System.out.println("Coordonnées invalides.");
        }
    }
    // Initialisation du WebView pour l'autocomplétion d'adresse
    private String adresseIncident;

    private void initAutocomplete() {
        String autoScript = """
            <!DOCTYPE html>
            <html>
            <head>
                <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDFUn6EuiuNTZ0TsETQ-BhCpmMcvOA7FME&libraries=places"></script>
                <script>
                    function initAutocomplete() {
                        var input = document.getElementById('autocomplete');
                        var autocomplete = new google.maps.places.Autocomplete(input);
                        autocomplete.addListener('place_changed', function() {
                            var place = autocomplete.getPlace();
                            if (place.geometry && place.geometry.location) {
                                var lat = place.geometry.location.lat();
                                var lng = place.geometry.location.lng();
                                // Met à jour l'élément caché et le titre du document pour transmettre les infos à Java
                                document.getElementById('address').innerText = place.formatted_address;
                                document.title = lat + "," + lng;
                            }
                        });
                    }
                </script>
            </head>
            <body onload="initAutocomplete()">
                <input id="autocomplete" type="text" placeholder="Entrez une adresse" style="width:100%;"/>
                <div id="address" style="display:none;"></div>
            </body>
            </html>
            """;

        WebEngine autoEngine = autocompleteView.getEngine();
        autoEngine.loadContent(autoScript);

        // Utiliser titleProperty pour détecter les changements du titre du document
        autoEngine.titleProperty().addListener((observable, oldTitle, newTitle) -> {
            if (newTitle != null && newTitle.contains(",")) {
                // Récupère l'adresse depuis l'élément caché
                String address = (String) autoEngine.executeScript("document.getElementById('address').innerText");
                // Met à jour la propriété liée au Label
                dynamicLocalisation.set(address);
                System.out.println("Adresse sélectionnée : " + address + " (Titre : " + newTitle + ")");
            }
        });
    }


    private void chargerIncidents() {
        incidentsList = FXCollections.observableArrayList(incidentService.getAllIncidents());
        incidentTable.setItems(incidentsList);
    }



    @FXML
    private void ajouterIncident() {
        String typeIncident = typeIncidentComboBox.getValue();
        String description = descriptionField.getText();
        String localisation = dynamicLocalisation.get();
        String statut = "En attente";
        String image = imagePath;




        // Vérification des champs obligatoires
        if (typeIncident == null || typeIncident.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner le type d'incident.");
            return;
        }
        if (description == null || description.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une description.");
            return;
        }


        if (imagePath == null || imagePath.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une image.");
            return;
        }
        if (localisation == null || localisation.isEmpty()) {
            showAlert("Erreur", "Veuillez indiquer la localisation.");
            return;
        }

        // Création de l'objet Incident avec l'utilisateur connecté
        Incident incident = new Incident(typeIncident, description, localisation, statut, latitude, longitude, image);

        // Ajout de l'incident dans la base de données
        incidentService.ajouterIncident(incident);



        // Réinitialisation des champs après l'ajout
        clearFields();

        // Affichage d'un message de confirmation
        showSuccessAlert("Succès", "Incident ajouté avec succès");
    }


    /**
     * Méthode pour choisir une image depuis l'ordinateur
     */
    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagePath = file.toURI().toString(); // Convertir le chemin en URI
            Image image = new Image(imagePath);
            imageView.setImage(image);
            System.out.println("path : " + imagePath );
        }
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
    private void modifierIncident() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }
        String typeIncident = typeIncidentComboBox.getValue();
        String description = descriptionField.getText();
        String statut = "Non résolu";
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

        // Mise à jour de l'incident sélectionné
        selectedIncident.setTypeIncident(typeIncident);
        selectedIncident.setDescription(description);
        selectedIncident.setLocalisation(localisation);
        selectedIncident.setStatut(statut);
        selectedIncident.setLatitude(latitude);
        selectedIncident.setLongitude(longitude);
        selectedIncident.setImage(imagePath);

        // Mise à jour de l'incident dans la base de données
        incidentService.modifierIncident(selectedIncident);
        chargerIncidents();
        clearFields();
        showSuccessAlert("Succès", "Incident modifié avec succès !");
    }
    @FXML
    private void CitoyenmodifierIncident() {
        Incident selectedIncident = incidentTable.getSelectionModel().getSelectedItem();

        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }
        String typeIncident = typeIncidentComboBox.getValue();
        String description = descriptionField.getText();
        String localisation = dynamicLocalisation.get();
        String statut = "Non résolu";
        String image = imagePath;
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




        // Mise à jour de l'incident sélectionné
        selectedIncident.setTypeIncident(typeIncident);
        selectedIncident.setDescription(description);
        selectedIncident.setLocalisation(localisation);
        selectedIncident.setLatitude(latitude);
        selectedIncident.setLongitude(longitude);
        selectedIncident.setImage(imagePath);

        // Mise à jour de l'incident dans la base de données
        incidentService.modifierIncident(selectedIncident);
        chargerIncidents();
        clearFields();
        showSuccessAlert("Succès", "Incident modifié avec succès !");
        incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                remplirChampsIncident(newSelection);
            }
        });
    }
    private void remplirChampsIncident(Incident incident) {



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



    @FXML
    public void onLocationSearch(KeyEvent event) {
        // Check if the user pressed Enter or a key to trigger the search
        if (event.getCode().toString().equals("ENTER") || event.getCode().toString().equals("TAB")) {
            String searchText = searchLocationField.getText();

            if (searchText != null && !searchText.trim().isEmpty()) {
                // Here we simulate getting location results from an API
                // Replace this with an actual API call to a geolocation service
                List<String> suggestions = fetchLocationSuggestions(searchText);

                // Update the ListView with the location suggestions
                ObservableList<String> observableSuggestions = FXCollections.observableArrayList(suggestions);
                locationListView.setItems(observableSuggestions);
            }
        }
    }
    private List<String> fetchLocationSuggestions(String searchText) {
        // Normally, you would call an API here, such as Google Places API
        // Example: https://maps.googleapis.com/maps/api/place/autocomplete/json?input=<searchText>&key=<API_KEY>

        // For now, we're simulating location suggestions
        List<String> suggestions = new ArrayList<>();
        suggestions.add(searchText + " - Location 1");
        suggestions.add(searchText + " - Location 2");
        suggestions.add(searchText + " - Location 3");

        return suggestions;
    }

    @FXML
    public void onLocationSelect() {
        String selectedLocation = locationListView.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            // Set the selected location to the label
            localisationLabel.setText(selectedLocation);


        }

    }
    @FXML
    private void goToajouterIncidentPage(javafx.event.ActionEvent event) {
        loadPage(event, "/ajouterincidentForm.fxml");
    }

    @FXML
    private void goTolisterIncidentPage(javafx.event.ActionEvent event) {
        loadPage(event, "/listeincidentForm.fxml");
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

