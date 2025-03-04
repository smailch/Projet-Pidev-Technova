package com.example.demo;

import com.example.demo.services.LampadaireScheduler;
import com.example.demo.utils.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/main.fxml"));

        // Set up the scene and stage
        Scene scene = new Scene(root, 1200, 800); // Increased aspect ratio
        primaryStage.setTitle("Add Quartier");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the scheduler
        DatabaseService databaseService = new DatabaseService(); // Assuming you have a DatabaseService class
        LampadaireScheduler scheduler = new LampadaireScheduler(databaseService);
        scheduler.startScheduler();
    }


    public static void main(String[] args) {
        launch(args);
    }
}