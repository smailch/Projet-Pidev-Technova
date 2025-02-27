package com.example.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private VBox rootContainer; // Root container for applying themes

    private String currentTheme; // Track the current theme

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Wait until rootContainer is attached to a Scene before applying the theme
        rootContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyThemeBasedOnTime();
            }
        });
    }

    /**
     * Switches the scene to the specified FXML file.
     *
     * @param event    The ActionEvent triggered by the button click.
     * @param fxmlFile The path to the FXML file to load.
     * @throws IOException If the FXML file cannot be loaded.
     */
    private void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        URL resourceUrl = getClass().getResource(fxmlFile);
        if (resourceUrl == null) {
            System.err.println("FXML file not found: " + fxmlFile);
            return;
        }

        Parent root = FXMLLoader.load(resourceUrl);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        // Apply the current theme to the new scene
        if (currentTheme != null) {
            URL cssResource = getClass().getResource(currentTheme);
            if (cssResource != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.err.println("CSS file not found: " + currentTheme);
            }
        }

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Applies the theme based on the current time of day.
     */
    private void applyThemeBasedOnTime() {
        LocalTime now = LocalTime.now();
        String theme;

        // Define the time range for the dark theme (e.g., 6 PM to 6 AM)
        if (now.isAfter(LocalTime.of(18, 0)) || now.isBefore(LocalTime.of(6, 0))) {
            theme = "/com/example/demo/dark-theme.css";
        } else {
            theme = "/com/example/demo/light-theme.css";
        }

        // Apply the selected theme
        applyTheme(theme);
    }

    /**
     * Applies the specified theme to the current scene.
     *
     * @param theme The name of the CSS file to apply.
     */
    private void applyTheme(String theme) {
        currentTheme = theme; // Update the current theme
        if (rootContainer != null && rootContainer.getScene() != null) {
            Scene scene = rootContainer.getScene();
            scene.getStylesheets().clear();

            URL cssResource = getClass().getResource(theme);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.err.println("CSS file not found: " + theme);
            }
        }
    }

    /**
     * Toggles between the dark and light themes.
     */
    @FXML
    private void toggleTheme() {
        if ("/com/example/demo/dark-theme.css".equals(currentTheme)) {
            applyTheme("/com/example/demo/light-theme.css");
        } else {
            applyTheme("/com/example/demo/dark-theme.css");
        }
    }

    // Navigation methods
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
        switchScene(event, "/com/example/demo/page5.fxml");
    }

    @FXML
    private void goToPage6(ActionEvent event) throws IOException {
        switchScene(event, "/com/example/demo/page6.fxml");
    }
}
