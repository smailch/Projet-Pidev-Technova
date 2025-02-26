package org.example.demo1.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.demo1.models.Poubelle_Intelligente;
import org.example.demo1.services.PoubelleIntelligenteService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PoubelleListController implements Initializable {

    @FXML
    private TableView<Poubelle_Intelligente> poubelleTable;

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    private final PoubelleIntelligenteService poubelleService = new PoubelleIntelligenteService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshTable();
    }

    @FXML
    public void refreshTable() {
        List<Poubelle_Intelligente> poubelles = poubelleService.getAllPoubelles();
        poubelleTable.getItems().setAll(poubelles);
    }

    @FXML
    public void handleDeleteButton() {
        Poubelle_Intelligente selectedPoubelle = poubelleTable.getSelectionModel().getSelectedItem();

        if (selectedPoubelle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Poubelle Selected", "Please select a poubelle to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Poubelle");
        confirmAlert.setContentText("Are you sure you want to delete the selected poubelle?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                poubelleService.deletePoubelle(selectedPoubelle.getId());
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Poubelle Deleted", "The poubelle was deleted successfully.");
            }
        });
    }

    @FXML
    public void handleUpdateButton() {
        Poubelle_Intelligente selectedPoubelle = poubelleTable.getSelectionModel().getSelectedItem();

        if (selectedPoubelle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Poubelle Selected", "Please select a poubelle to update.");
            return;
        }

        try {
            // Load the edit dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/UpdatePoubelleDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // Get the controller
            EditPoubelleController editController = loader.getController();

            // Pass the selected poubelle to the edit controller
            editController.setPoubelle(selectedPoubelle);

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Update Poubelle");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Pass the dialog instance to the controller
            editController.setDialog(dialog); // Ensure this line is executed

            // Show the dialog and wait for the user's response
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Refresh the table to reflect the changes
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Poubelle Updated", "The poubelle was updated successfully.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Open Edit Dialog", "An error occurred while trying to open the edit dialog.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}