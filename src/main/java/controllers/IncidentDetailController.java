package controllers;

import entities.Incident;
import entities.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.text.SimpleDateFormat;

public class IncidentDetailController {

    @FXML
    private Label incidentIdLabel;
    @FXML
    private Label incidentStatusLabel;
    @FXML
    private Label incidentUserIdLabel;
    @FXML
    private Label incidentUserNameLabel;
    @FXML
    private Label incidentUserEmailLabel;
    @FXML
    private Label incidentDateLabel; // Assurez-vous que cet ID correspond au fichier FXML

    public void initialize(Incident selectedIncident, Utilisateur utilisateur) {
        // Display incident details in the labels
        if (selectedIncident != null) {
            incidentIdLabel.setText("ID: " + selectedIncident.getId());
            incidentStatusLabel.setText("Statut: " + selectedIncident.getStatut());

            // Formater et afficher la date de l'incident
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            incidentDateLabel.setText("Date: " + dateFormat.format(selectedIncident.getDateSignalement()));
        }

        if (utilisateur != null) {
            incidentUserIdLabel.setText("Utilisateur ID: " + utilisateur.getId());
            incidentUserNameLabel.setText("Nom: " + utilisateur.getNom() + " " + utilisateur.getPrenom());
            incidentUserEmailLabel.setText("Email: " + utilisateur.getEmail());
        }
    }
}
