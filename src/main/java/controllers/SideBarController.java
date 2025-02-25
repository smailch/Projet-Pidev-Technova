package controllers;

import entities.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.UtilisateurService;
import javafx.event.ActionEvent;


public class SideBarController {
    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final ObservableList<Utilisateur> allUsers = FXCollections.observableArrayList();

    @FXML
    public void Logout(Event event){
        Utilisateur utilisateur = SharedDataController.getInstance().getUtilisateur();
        utilisateurService.deconnexion(utilisateur);
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Login.fxml", currentStage, titleBar);
    }
    @FXML
    private void onDashboardClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Sample.fxml", currentStage, titleBar);
    }

    // Utilisateur section click action
    @FXML
    private void onUtilisateurClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/gestionutilisateurs.fxml", currentStage, titleBar);
    }

    // Finances section click action
    @FXML
    private void onFinancesClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DeclarationRevenues.fxml", currentStage, titleBar);
    }

    // Dechet section click action
    @FXML
    private void onDechetClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Profil.fxml", currentStage, titleBar);
    }

    // Lampadaires section click action
    @FXML
    private void onLampadairesClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Profil.fxml", currentStage, titleBar);
    }

    // Incidents section click action
    @FXML
    private void onIncidentsClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/Profil.fxml", currentStage, titleBar);
    }

    // Paperasses section click action
    @FXML
    private void onPaperassesClick(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/DocumentAdministratif.fxml", currentStage, titleBar);
    }
}
