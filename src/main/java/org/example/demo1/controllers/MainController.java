package org.example.demo1.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MainController {

    @FXML
    private Button button1, button2, button3, button4, button5, button6;

    // Method for navigating to add_zone.fxml
    @FXML
    private void goToAddZone(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/add_zone.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // Method for navigating to add_camion.fxml
    @FXML
    private void goToAddCamion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/AddCamion.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // Method for navigating to view_all_camion.fxml
    @FXML
    private void goToViewAllCamion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/CamionCollecteView.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // Method for navigating to add_poubelle.fxml
    @FXML
    private void goToAddPoubelle(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/AddPoubelle.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // Method for navigating to listpoubelle.fxml
    @FXML
    private void goToListPoubelle(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/poubelle_list.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // Method for handling other buttons (Button 5, 6)
    @FXML
    private void handleButtonClick(ActionEvent event) {
        Button source = (Button) event.getSource();
        System.out.println(source.getText() + " clicked");
    }
}
