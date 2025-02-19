package controllers;

import entities.Lampadaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.LampadaireService;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeLampadaireController implements Initializable {

    @FXML
    private TableColumn<Lampadaire, Integer> colId;

    @FXML
    private TableColumn<Lampadaire, String> colLocalisation;

    @FXML
    private TableColumn<Lampadaire, String> colEtat;

    @FXML
    private TableColumn<Lampadaire, String> colConsommation;

    @FXML
    private TableColumn<Lampadaire, String> colNomQuartier;

    @FXML
    private TableColumn<Lampadaire, Void> colAction;

    @FXML
    private TableView<Lampadaire> tableLampadaire;

    private LampadaireService lampadaireService = new LampadaireService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherLampadaires();
    }

    public void afficherLampadaires() {
        List<Lampadaire> lampadaires = lampadaireService.getAllData();
        ObservableList<Lampadaire> data = FXCollections.observableArrayList(lampadaires);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colEtat.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isEtat() ? "Allumé" : "Éteint"));
        colConsommation.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getConsommation())));
        colNomQuartier.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getQuartier().getNom()));

        tableLampadaire.setItems(data);
        addButtonToTable(); // Ajoute les boutons Modifier et Supprimer à chaque ligne
    }

    private void addButtonToTable() {
        Callback<TableColumn<Lampadaire, Void>, TableCell<Lampadaire, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Lampadaire, Void> call(final TableColumn<Lampadaire, Void> param) {
                        return new TableCell<>() {
                            private final Button btnModifier = new Button("Modifier");
                            private final Button btnSupprimer = new Button("Supprimer");

                            {
                                // Logique du bouton Modifier
                                btnModifier.setOnAction(event -> {
                                    Lampadaire lampadaire = getTableView().getItems().get(getIndex());
                                    openModifierPage(lampadaire);  // Ouvre la page de modification
                                });
                                btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                                // Logique du bouton Supprimer
                                btnSupprimer.setOnAction(event -> {
                                    Lampadaire lampadaire = getTableView().getItems().get(getIndex());
                                    supprimerLampadaire(lampadaire);
                                });
                                btnSupprimer.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    // Ajoute les deux boutons à chaque ligne
                                    HBox hBox = new HBox(10, btnModifier, btnSupprimer);
                                    setGraphic(hBox);
                                }
                            }
                        };
                    }
                };

        colAction.setCellFactory(cellFactory);
    }

    private void openModifierPage(Lampadaire lampadaire) {
        try {
            // Charger la scène de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterLampadaire.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et passer le lampadaire à modifier
            ajouterLampadaireController modifierController = loader.getController();
            modifierController.setLampadaire(lampadaire);  // Passer le lampadaire sélectionné

            // Afficher la page de modification
            Stage stage = (Stage) tableLampadaire.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerLampadaire(Lampadaire lampadaire) {
        lampadaireService.deleteEntity(lampadaire);
        afficherLampadaires();  // Rafraîchir la table après suppression
    }

    // Méthode pour mettre à jour la table après ajout ou modification d'un lampadaire
    public void refreshTable() {
        afficherLampadaires();
    }
}