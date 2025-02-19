package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.util.logging.Logger;

public class MenuController {

    private static final Logger logger = Logger.getLogger(MenuController.class.getName());

    @FXML
    private Button buttLISTLamp;

    @FXML
    private Button buttLISTQuart;

    @FXML
    private Button buttLamp;

    @FXML
    private Button buttQuart;

    // Méthode pour aller à l'interface d'ajout de lampadaire
    @FXML
    void goToAjouterLampadaire() {
        logger.info("Navigating to 'Ajouter Lampadaire' interface...");
        try {
            // Charger l'interface Ajouter Lampadaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterLampadaire.fxml"));
            Parent root = loader.load();
            // Obtenir la scène actuelle et changer l'interface
            Stage stage = (Stage) buttLamp.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error loading 'Ajouter Lampadaire' interface: " + e.getMessage());
            showAlert("Erreur de chargement", "Erreur de chargement de l'interface", e.getMessage());
        }
    }

    // Méthode pour aller à l'interface Liste des lampadaires
    @FXML
    void goToListeLampadaire() {
        logger.info("Navigating to 'Liste Lampadaires' interface...");
        try {
            // Charger l'interface Liste Lampadaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeLampadaire.fxml"));
            Parent root = loader.load();
            // Obtenir la scène actuelle et changer l'interface
            Stage stage = (Stage) buttLISTLamp.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error loading 'Liste Lampadaires' interface: " + e.getMessage());
            showAlert("Erreur de chargement", "Erreur de chargement de l'interface", e.getMessage());
        }
    }

    // Méthode pour afficher une alerte en cas d'erreur
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthodes pour gérer les autres actions (ajouter et lister les quartiers)
    @FXML
    void goToAjouterQuartier() {
        logger.info("Navigating to 'Ajouter Quartier' interface...");
        try {
            // Charger l'interface Ajouter Quartier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterQuartier.fxml"));
            Parent root = loader.load();
            // Obtenir la scène actuelle et changer l'interface
            Stage stage = (Stage) buttQuart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error loading 'Ajouter Quartier' interface: " + e.getMessage());
            showAlert("Erreur de chargement", "Erreur de chargement de l'interface", e.getMessage());
        }
    }

    @FXML
    void goToListeQuartier() {
        logger.info("Navigating to 'Ajouter Quartier' interface...");
        try {
            // Charger l'interface Ajouter Quartier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeQuartier.fxml"));
            Parent root = loader.load();
            // Obtenir la scène actuelle et changer l'interface
            Stage stage = (Stage) buttQuart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.severe("Error loading 'Ajouter Quartier' interface: " + e.getMessage());
            showAlert("Erreur de chargement", "Erreur de chargement de l'interface", e.getMessage());
        }    }
}
