package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private void goToIncidentPage(ActionEvent event) {
        loadPage(event, "/ajouterincidentForm.fxml");
    }

    @FXML
    private void goToServicePage(ActionEvent event) {
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
