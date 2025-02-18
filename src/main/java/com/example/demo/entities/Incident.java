package com.example.demo.entities;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.sql.Timestamp;
import java.util.List;

public class Incident {

    private IntegerProperty id;
    private SimpleStringProperty typeIncident;
    private StringProperty description;
    private StringProperty statut;
    private ObjectProperty<Timestamp> dateSignalement;
    private IntegerProperty serviceAffecte;
    private StringProperty localisation;
    private DoubleProperty latitude;
    private DoubleProperty longitude;
    private StringProperty image; // New image field
    private IntegerProperty utilisateurId;
    // Complete constructor
    public Incident(int id, String typeIncident, String description, String localisation, String statut, Timestamp dateSignalement, int serviceAffecte, double latitude, double longitude, String image, int utilisateurId) {
        this.id = new SimpleIntegerProperty(id);
        this.typeIncident = new SimpleStringProperty(typeIncident);
        this.description = new SimpleStringProperty(description);
        this.localisation = new SimpleStringProperty(localisation);
        this.statut = new SimpleStringProperty(statut);
        this.dateSignalement = new SimpleObjectProperty<>(dateSignalement);
        this.serviceAffecte = new SimpleIntegerProperty(serviceAffecte);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.image = new SimpleStringProperty(image);  // Initialize image field
        this.utilisateurId = new SimpleIntegerProperty(utilisateurId);
    }

    // Constructor for adding new incidents
    public Incident(String typeIncident, String description, String localisation, String statut, double latitude, double longitude, String image, int utilisateurId ) {
        this.typeIncident = new SimpleStringProperty(typeIncident);
        this.description = new SimpleStringProperty(description);
        this.localisation = new SimpleStringProperty(localisation);
        this.statut = new SimpleStringProperty(statut);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.image = new SimpleStringProperty(image);  // Initialize image field
        this.utilisateurId = new SimpleIntegerProperty(utilisateurId);
    }

    public Incident(int id, String typeIncident, String description, String localisation, String statut, Timestamp dateSignalement, int serviceAffecte, double latitude, double longitude, String image) {
        this.id = new SimpleIntegerProperty(id);
        this.typeIncident = new SimpleStringProperty(typeIncident);
        this.description = new SimpleStringProperty(description);
        this.localisation = new SimpleStringProperty(localisation);
        this.statut = new SimpleStringProperty(statut);
        this.dateSignalement = new SimpleObjectProperty<>(dateSignalement);
        this.serviceAffecte = new SimpleIntegerProperty(serviceAffecte);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.image = new SimpleStringProperty(image);

    }

    public Incident(String typeIncident, String description, String localisation, String statut, double latitude, double longitude, String image) {
        this.typeIncident = new SimpleStringProperty(typeIncident);
        this.description = new SimpleStringProperty(description);
        this.localisation = new SimpleStringProperty(localisation);
        this.statut = new SimpleStringProperty(statut);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.image = new SimpleStringProperty(image);

    }

    public Incident(String typeIncident, String description, String localisation, double latitude, double longitude, String imagePath) {
        this.typeIncident = new SimpleStringProperty(typeIncident);
        this.description = new SimpleStringProperty(description);
        this.localisation = new SimpleStringProperty(localisation);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.image = new SimpleStringProperty(imagePath);

    }


    // Getters for each property
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty typeIncidentProperty() {
        return typeIncident;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty localisationProperty() {
        return localisation;
    }

    public StringProperty statutProperty() {
        return statut;
    }

    public ObjectProperty<Timestamp> dateSignalementProperty() {
        return dateSignalement;
    }

    public IntegerProperty serviceAffecteProperty() {
        return serviceAffecte;
    }

    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    public StringProperty imageProperty() { // Getter for image property
        return image;
    }

    // Getters and setters for non-property fields (optional)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTypeIncident() {
        return typeIncident.get();
    }

    public void setTypeIncident(String typeIncident) {
        this.typeIncident.set(typeIncident);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocalisation() {
        return localisation.get();
    }

    public void setLocalisation(String localisation) {
        this.localisation.set(localisation);
    }

    public String getStatut() {
        return statut.get();
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    public Timestamp getDateSignalement() {
        return dateSignalement.get();
    }

    public void setDateSignalement(Timestamp dateSignalement) {
        this.dateSignalement.set(dateSignalement);
    }

    public int getServiceAffecte() {
        return serviceAffecte.get();
    }

    public void setServiceAffecte(int serviceAffecte) {
        this.serviceAffecte.set(serviceAffecte);
    }

    public double getLatitude() {
        return latitude.get();
    }

    public void setLatitude(double latitude) {
        this.latitude.set(latitude);
    }

    public double getLongitude() {
        return longitude.get();
    }

    public void setLongitude(double longitude) {
        this.longitude.set(longitude);
    }

    public String getImage() {
        return image.get();
    }


    public void setImage(String image) {
        this.image.set(image);
    }
    public int getUtilisateurId() {
        return utilisateurId.get();  // Getter pour utilisateurId
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId.set(utilisateurId);  // Setter pour utilisateurId
    }

    public ObservableValue<String> imagePathProperty() {
        return image;
    }
    // Méthode pour obtenir le nom du service en fonction de l'ID du service
    public String serviceAffecteNomProperty(List<ServiceIntervention> services) {
        for (ServiceIntervention service : services) {
            if (service.getId() == this.getServiceAffecte()) {
                return service.getNomService(); // Renvoie le nom du service
            }
        }
        return "Service inconnu"; // Valeur par défaut si aucun service n'est trouvé
    }

}
