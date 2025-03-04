package controllers;

import entities.Utilisateur;
import services.IncidentService;
import services.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import entities.Incident;

import java.io.IOException;
import java.util.List;

public class HistoriqueCitoyenController {

    @FXML
    private TableView<Incident> tableIncidentsResolus;

    @FXML
    private TableColumn<Incident, Integer> colId;

    @FXML
    private TableColumn<Incident, String> colType;

    @FXML
    private TableColumn<Incident, String> colDescription;

    @FXML
    private TableColumn<Incident, String> colLocalisation;


    @FXML
    private TableColumn<Incident, String> colDateSignalement;

    @FXML
    private TableColumn<Incident, String> colDateResolution;

    private IncidentService incidentService = new IncidentService(); // Service pour la gestion des incidents

    @FXML
    public void initialize() {
        loadHistoriqueCitoyen(); // Charger les incidents résolus lors de l'initialisation
    }

    // Méthode pour charger les incidents résolus pour l'utilisateur connecté
    private void loadHistoriqueCitoyen() {
        // Récupérer l'utilisateur connecté
        Utilisateur utilisateur = SessionManager.getInstance().getUtilisateurConnecte();
        if (utilisateur == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }

        // Appeler le service pour récupérer les incidents résolus de l'utilisateur connecté
        List<Incident> incidentsResolus = incidentService.getIncidentsResolusPourCitoyen(utilisateur.getId());

        // Vérifier si la liste est vide
        if (incidentsResolus.isEmpty()) {
            System.out.println("Aucun incident résolu trouvé.");
        }

        // Associer les colonnes aux propriétés de la classe Incident
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeIncidentProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colLocalisation.setCellValueFactory(cellData -> cellData.getValue().localisationProperty());

        colDateSignalement.setCellValueFactory(cellData -> cellData.getValue().dateSignalementProperty().asString());
        colDateResolution.setCellValueFactory(cellData ->
                new SimpleStringProperty(Incident.calculerDureeResolution(cellData.getValue()))
        );
        // Ajouter les incidents résolus au tableau
        tableIncidentsResolus.getItems().setAll(incidentsResolus);
    }

    // Méthode pour afficher une alerte en cas d'erreur


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
    @FXML
    private void supprimerIncident() {
        Incident selectedIncident = tableIncidentsResolus.getSelectionModel().getSelectedItem();
        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }

        IncidentService incidentService = new IncidentService();
        incidentService.supprimerHistoriqueIncident(selectedIncident.getId());

        loadHistoriqueCitoyen(); // Recharger les données après suppression
        tableIncidentsResolus.refresh(); // Forcer la mise à jour de l'affichage

        showSuccessAlert("Succès", "Incident supprimé avec succès !");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
