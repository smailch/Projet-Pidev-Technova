package controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavigationUtils {
    public static void switchPage(String fxmlPath, Stage stage, HBox customTitleBar) {
        try {
            // Load the new FXML page
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
            Parent newPage = loader.load();

            // Create main layout with title bar and new page content
            VBox layout = new VBox();
            layout.getChildren().addAll(customTitleBar, newPage);
            VBox.setVgrow(newPage, Priority.ALWAYS);

            // Set the new scene
            Scene scene = new Scene(layout);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HBox createCustomTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 10px;");
        titleBar.setPrefHeight(40);
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPadding(new Insets(10, 15, 10, 15));

        // Allow dragging the window
        titleBar.setOnMousePressed(event -> {
            titleBar.setUserData(new double[]{event.getSceneX(), event.getSceneY()});
        });

        titleBar.setOnMouseDragged(event -> {
            double[] offset = (double[]) titleBar.getUserData();
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
        // Classy, modern background color (neutral dark gray)
        titleBar.setStyle("-fx-background-color: #2C2F33; "
                + "-fx-border-width: 0 0 1 0; "
                + "-fx-border-color: #202225; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0, 0, 2);");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Minimize and close buttons
        StackPane minimizeButton = createCircleButton(Color.web("#FDFF8C"), () -> stage.setIconified(true));
        StackPane closeButton = createCircleButton(Color.web("#FF747D"), stage::close);

        // Add spacing between buttons
        HBox buttonContainer = new HBox(10, minimizeButton, closeButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        // Add components to title bar
        titleBar.getChildren().addAll(spacer, buttonContainer);

        return titleBar;
    }

    private static StackPane createCircleButton(Color color, Runnable action) {
        Circle circle = new Circle(7, color);
        StackPane buttonContainer = new StackPane(circle);
        buttonContainer.setPadding(new Insets(0)); // No extra padding
        buttonContainer.setOnMouseClicked(e -> action.run());

        // Hover effect: Slightly increase size
        buttonContainer.setOnMouseEntered(e -> circle.setRadius(7.2));
        buttonContainer.setOnMouseExited(e -> circle.setRadius(7));

        return buttonContainer;
    }

}
