package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import services.SessionManager;


import java.net.URL;

public class CardPaymentController {
    @FXML private WebView webView;



    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getUserId();
        int dossierId = SessionManager.getInstance().getDossierId();

        // Log to console in Java to confirm if values are correct
        System.out.println("User ID: " + userId);
        System.out.println("Dossier ID: " + dossierId);

        URL htmlUrl = getClass().getResource("/html/payment_form.html");
        if (htmlUrl == null) {
            System.err.println("HTML file not found!");
        } else {
            webView.getEngine().executeScript("window.userId = " + userId + ";");
            webView.getEngine().executeScript("window.dossierId = " + dossierId + ";");
            // Test if the JavaScript can access these variables
            webView.getEngine().executeScript("console.log('User ID from JavaScript:', window.userId);");
            webView.getEngine().executeScript("console.log('Dossier ID from JavaScript:', window.dossierId);");

            webView.getEngine().load(htmlUrl.toExternalForm());


        }
    }
    // This method will check the payment status
    @FXML
    public void redirectToFiscale(Event event){
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DossierFiscaleUser.fxml", currentStage, titleBar);
    }

}




