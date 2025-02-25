package com.example.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void goToPage1(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/add-quartier.fxml");
    }

    @FXML
    private void goToPage2(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/list-quartiers.fxml");
    }

    @FXML
    private void goToPage3(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/add-lampadaire.fxml");
    }

    @FXML
    private void goToPage4(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/lampadaire-list.fxml");
    }

    @FXML
    private void goToPage5(ActionEvent event) throws IOException {
        switchScene(event, "page5.fxml");
    }

    @FXML
    private void goToPage6(ActionEvent event) throws IOException {
        switchScene(event, "page6.fxml");
    }
}
