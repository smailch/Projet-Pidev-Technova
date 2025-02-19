package controllers;

import entities.Quartier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.QuartierService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class ListeQuartierController implements Initializable {

    @FXML
    private TableView<Quartier> tableQuartier;

    @FXML
    private TableColumn<Quartier, Integer> colId;

    @FXML
    private TableColumn<Quartier, String> colNom;

    @FXML
    private TableColumn<Quartier, Integer> colNbLamp;

    @FXML
    private TableColumn<Quartier, Double> colConsomTot;

    @FXML
    private Button btnModify;

    @FXML
    private Button btnDelete;

    private ObservableList<Quartier> quartierList = FXCollections.observableArrayList();
    private QuartierService quartierService = new QuartierService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind table columns to Quartier properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNbLamp.setCellValueFactory(new PropertyValueFactory<>("nbLamp"));
        colConsomTot.setCellValueFactory(new PropertyValueFactory<>("consomTot"));

        // Load data into the table
        refreshTable();
    }

    public void refreshTable() {
        quartierList.setAll(quartierService.getAllData());
        tableQuartier.setItems(quartierList);
        tableQuartier.refresh();
    }

    @FXML
    private void modifySelectedQuartier() {
        Quartier selectedQuartier = tableQuartier.getSelectionModel().getSelectedItem();

        if (selectedQuartier == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un quartier à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModifierQuartier.fxml"));
            Parent root = loader.load();

            ajouterQuartierController modifyController = loader.getController();
            modifyController.setQuartier(selectedQuartier, this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Quartier");
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    private void deleteSelectedQuartier() {
        Quartier selectedQuartier = tableQuartier.getSelectionModel().getSelectedItem();

        if (selectedQuartier == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un quartier à supprimer.");
            return;
        }

        // Confirmation dialog before deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer Quartier");
        alert.setContentText("Voulez-vous vraiment supprimer ce quartier ?");

        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            quartierService.deleteEntity(selectedQuartier.getId());
            showAlert("Succès", "Le quartier a été supprimé avec succès.");
            refreshTable();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}