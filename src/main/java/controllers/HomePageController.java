package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HomePageController {

    @FXML
    private VBox sidebar;

    // Method to show the full sidebar when mouse hovers
    public void showSidebar() {
        sidebar.getStyleClass().add("expanded");
    }

    // Method to hide the sidebar when mouse moves away
    public void hideSidebar() {
        sidebar.getStyleClass().remove("expanded");
    }

}
