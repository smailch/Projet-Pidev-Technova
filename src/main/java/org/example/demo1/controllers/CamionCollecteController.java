package org.example.demo1.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.demo1.models.Camion_Collecte;
import org.example.demo1.models.Zone;
import org.example.demo1.services.CamionCollecteService;
import org.example.demo1.services.ZoneService;

import java.io.IOException;

public class CamionCollecteController {

    @FXML
    private TableView<Camion_Collecte> camionTable;

    @FXML
    private TableColumn<Camion_Collecte, Integer> idColumn;

    @FXML
    private TableColumn<Camion_Collecte, Double> capaciteMaxColumn;

    @FXML
    private TableColumn<Camion_Collecte, String> statutColumn;

    @FXML
    private TableColumn<Camion_Collecte, String> zoneNameColumn;

    @FXML
    private TableColumn<Camion_Collecte, Void> actionColumn;

    private final CamionCollecteService camionService;
    private final ZoneService zoneService;

    public CamionCollecteController() {
        this.camionService = new CamionCollecteService();
        this.zoneService = new ZoneService();
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadCamionData();
        addButtonsToTable();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        capaciteMaxColumn.setCellValueFactory(new PropertyValueFactory<>("capacite_max"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        zoneNameColumn.setCellValueFactory(cellData -> {
            Zone zone = zoneService.getZoneById(cellData.getValue().getZone_id());
            return (zone != null) ? new javafx.beans.property.SimpleStringProperty(zone.getLocation()) : new javafx.beans.property.SimpleStringProperty("Unknown");
        });
    }

    private void loadCamionData() {
        ObservableList<Camion_Collecte> camions = FXCollections.observableArrayList(camionService.getAllCamions());
        camionTable.setItems(camions);
    }

    private void addButtonsToTable() {
        addDeleteButton();
        addUpdateButton();
    }

    private void addDeleteButton() {
        actionColumn = new TableColumn<>("Actions"); // Create a new instance
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Camion_Collecte, Void> call(final TableColumn<Camion_Collecte, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            Camion_Collecte camion = getTableView().getItems().get(getIndex());
                            if (camionService.deleteCamion(camion.getId())) {
                                loadCamionData();
                                System.out.println("Camion deleted successfully.");
                            } else {
                                System.err.println("Failed to delete camion with ID: " + camion.getId());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : deleteButton);
                    }
                };
            }
        });
        camionTable.getColumns().add(actionColumn); // Add the action column only once
    }

    private void addUpdateButton() {
        TableColumn<Camion_Collecte, Void> updateColumn = new TableColumn<>("Update");

        updateColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Camion_Collecte, Void> call(final TableColumn<Camion_Collecte, Void> param) {
                return new TableCell<>() {
                    private final Button updateButton = new Button("Update");

                    {
                        updateButton.setOnAction(event -> {
                            Camion_Collecte camion = getTableView().getItems().get(getIndex());
                            openEditCamionPopup(camion);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : updateButton);
                    }
                };
            }
        });

        camionTable.getColumns().add(updateColumn); // Add the update column only once
    }

    private void openEditCamionPopup(Camion_Collecte camion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo1/EditCamion.fxml"));
            Parent root = loader.load();

            EditCamionController controller = loader.getController();
            controller.setCamion(camion);

            Stage stage = new Stage();
            stage.setTitle("Edit Camion");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCamionData();
        } catch (IOException e) {
            System.err.println("Error loading EditCamion popup: " + e.getMessage());
        }
    }
}