package org.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.demo1.utils.DatabaseService;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check database connection when the application starts
        DatabaseService databaseService = new DatabaseService();
        databaseService.getConnection(); // This will print the connection status in the terminal

        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/demo1/Main.fxml"));

        // Set up the scene and stage
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Gestion Dechets");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}