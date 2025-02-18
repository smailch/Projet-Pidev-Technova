package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class Home extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Login.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Set the window title
            stage.setTitle("CIVISMART");

            // Set the logo
            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/logo2.png")));
            stage.getIcons().add(logo);

            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur de lecture: " + e.getMessage());
        }
    }
}
