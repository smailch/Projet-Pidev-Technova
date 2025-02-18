package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Home extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    private StackPane contentPane; // This will hold pages

    @Override
    public void start(Stage stage) {
        try {
            // Load the first page (Login)
            Parent root = FXMLLoader.load(getClass().getResource("/DeclarationRevenues.fxml"));

            // Create the content pane and set it as a container for switching pages
            contentPane = new StackPane();
            contentPane.getChildren().add(root); // Initialize with Login page

            // Custom title bar from NavigationUtils
            HBox titleBar = NavigationUtils.createCustomTitleBar(stage);

            // Main layout (title bar + content)
            VBox layout = new VBox();
            layout.getChildren().addAll(titleBar, contentPane);
            VBox.setVgrow(contentPane, Priority.ALWAYS);

            // Scene setup
            Scene scene = new Scene(layout);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
