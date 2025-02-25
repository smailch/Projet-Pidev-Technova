package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Home extends Application {
    private StackPane contentPane;

    @Override
    public void start(Stage stage) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));

            contentPane = new StackPane();
            contentPane.getChildren().add(root); // Initialize with Login page

            HBox titleBar = NavigationUtils.createCustomTitleBar(stage);

            VBox layout = new VBox();
            layout.getChildren().addAll(titleBar, contentPane);
            VBox.setVgrow(contentPane, Priority.ALWAYS);

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
