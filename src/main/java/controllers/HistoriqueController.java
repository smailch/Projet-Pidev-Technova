package controllers;

import entities.Incident;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.util.List;
import services.IncidentService;

public class HistoriqueController {

    @FXML
    private TableView<Incident> tableIncidents;

    @FXML
    private TableColumn<Incident, Integer> colId;

    @FXML
    private TableColumn<Incident, String> colType;

    @FXML
    private TableColumn<Incident, String> colDescription;

    @FXML
    private TableColumn<Incident, String> colLocalisation;

    @FXML
    private TableColumn<Incident, String> colStatut;

    @FXML
    private TableColumn<Incident, String> colDateSignalement;

    @FXML
    private TableColumn<Incident, String> colDateResolution;

    private Incident incidentService = new Incident ();

    @FXML
    public void initialize() {
        loadHistorique(); // Charger les incidents lors de l'initialisation
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
    private void supprimerIncident() {
        Incident selectedIncident = tableIncidents.getSelectionModel().getSelectedItem();
        if (selectedIncident == null) {
            showAlert("Erreur", "Aucun incident sélectionné.");
            return;
        }

        IncidentService incidentService = new IncidentService();
        incidentService.supprimerHistoriqueIncident(selectedIncident.getId());

        loadHistorique(); // Recharger les données après suppression
        tableIncidents.refresh(); // Forcer la mise à jour de l'affichage

        showSuccessAlert("Succès", "Incident supprimé avec succès !");
    }


    private void loadHistorique() {
        IncidentService incidentService = new IncidentService();
        List<Incident> incidents = incidentService.getIncidentsResolu();

        if (incidents == null || incidents.isEmpty()) {
            System.out.println("Aucun incident résolu trouvé.");
            tableIncidents.getItems().clear(); // Vider le tableau si aucun incident
            return;
        }

        // Effacer les anciens incidents
        tableIncidents.getItems().clear();

        // Associer les colonnes
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeIncidentProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colLocalisation.setCellValueFactory(cellData -> cellData.getValue().localisationProperty());
        colStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
        colDateSignalement.setCellValueFactory(cellData -> cellData.getValue().dateSignalementProperty().asString());
        colDateResolution.setCellValueFactory(cellData -> cellData.getValue().dateResolutionProperty().asString());

        // Ajouter les incidents au tableau
        tableIncidents.getItems().setAll(incidents);
        tableIncidents.refresh(); // Rafraîchir le tableau
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