package com.example.demo.controllers;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import com.example.demo.services.LampadaireService;
import com.example.demo.services.QuartierService;
import com.example.demo.utils.PDFGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LampadaireListController {

    @FXML
    private TableView<Lampadaire> lampadaireTable;

    @FXML
    private TableColumn<Lampadaire, Integer> idColumn;

    @FXML
    private TableColumn<Lampadaire, String> localisationColumn;

    @FXML
    private TableColumn<Lampadaire, Boolean> etatColumn;

    @FXML
    private TableColumn<Lampadaire, Double> consommationColumn;


    @FXML
    private TableColumn<Lampadaire, String> quartierNameColumn;

    @FXML
    private TableColumn<Lampadaire, String> dateInstallationColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    private LampadaireService lampadaireService = new LampadaireService();

    @FXML
    public void initialize() {
        // Bind columns to Lampadaire properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        localisationColumn.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        consommationColumn.setCellValueFactory(new PropertyValueFactory<>("consommation"));
        dateInstallationColumn.setCellValueFactory(new PropertyValueFactory<>("date_installation"));

        // Add a new column for Quartier name
        quartierNameColumn.setCellValueFactory(cellData -> {
            int idQuartier = cellData.getValue().getId_quartier();
            QuartierService quartierService = new QuartierService();
            try {
                Quartier quartier = quartierService.getQuartierById(idQuartier);
                if (quartier != null) {
                    return new SimpleStringProperty(quartier.getNom());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("Unknown");
        });

        // Load data
        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        try {
            List<Lampadaire> lampadaires = lampadaireService.getAllLampadaires();
            lampadaireTable.getItems().setAll(lampadaires);
        } catch (Exception e) {
            showAlert("Error", "An error occurred while loading lampadaires: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Lampadaire selectedLampadaire = lampadaireTable.getSelectionModel().getSelectedItem();
        if (selectedLampadaire != null) {
            try {
                lampadaireService.deleteLampadaire(selectedLampadaire.getId());
                handleRefresh(); // Refresh the table after deletion
            } catch (Exception e) {
                showAlert("Error", "An error occurred while deleting the lampadaire: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a lampadaire to delete.");
        }
    }

    @FXML
    private void handleUpdate() {
        Lampadaire selectedLampadaire = lampadaireTable.getSelectionModel().getSelectedItem();
        if (selectedLampadaire != null) {
            try {
                // Load the update dialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/UpdateLampadaireDialog.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Update Lampadaire");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(loader.load()));

                // Pass the selected Lampadaire to the dialog controller
                UpdateLampadaireDialogController controller = loader.getController();
                controller.setSelectedLampadaire(selectedLampadaire);

                // Show the dialog and wait for it to close
                dialogStage.showAndWait();

                // Refresh the table after updating
                handleRefresh();
            } catch (IOException e) {
                showAlert("Error", "An error occurred while loading the update dialog: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a lampadaire to update.");
        }
    }

    @FXML
    private void handleGeneratePDF() {
        try {
            // Récupérer la liste des lampadaires
            List<Lampadaire> lampadaires = lampadaireService.getAllLampadaires();

            // Chemin de sortie du fichier PDF
            String filePath = "lampadaires_list.pdf";

            // Générer le PDF
            PDFGenerator.generateLampadairePDF(lampadaires, filePath);

            // Afficher un message de succès
            showAlert("Success", "PDF generated successfully at: " + filePath);
        } catch (Exception e) {
            showAlert("Error", "An error occurred while generating the PDF: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}