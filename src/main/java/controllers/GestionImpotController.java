package controllers;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GestionImpotController {
    public void redirectToDeclarations(ActionEvent event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DeclarationRevenues.fxml", currentStage, titleBar);
    }

    @FXML
    public void redirectToFiscale(Event event){
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DossierFiscaleUser.fxml", currentStage, titleBar);
    }


}
