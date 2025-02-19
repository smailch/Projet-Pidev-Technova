package controllers;

import entities.Quartier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.QuartierService;

import java.io.IOException;

public class ajouterQuartierController {

    @FXML
    private Button button;

    @FXML
    private TextField consomtottxtf;

    @FXML
    private TextField idtxtf;

    @FXML
    private TextField nblamptxtf;

    @FXML
    private TextField nomtxtf;

    private QuartierService quartierService = new QuartierService();
    private Quartier currentQuartier; // To store the Quartier being modified
    private ListeQuartierController parentController; // Reference to the parent controller

    /**
     * Sets the Quartier data for modification.
     *
     * @param quartier  The Quartier to modify.
     * @param controller The parent controller (ListeQuartierController).
     */
    public void setQuartier(Quartier quartier, ListeQuartierController controller) {
        this.currentQuartier = quartier;
        this.parentController = controller;

        // Populate the fields with the Quartier data
        idtxtf.setText(String.valueOf(quartier.getId()));
        nomtxtf.setText(quartier.getNom());
        nblamptxtf.setText(String.valueOf(quartier.getNbLamp()));
        consomtottxtf.setText(String.valueOf(quartier.getConsomTot()));
    }

    @FXML
    void ajouterQuartierAction(ActionEvent event) {
        try {
            // Validate input fields
            if (!validateInputs()) {
                return;
            }

            // Retrieve values from the fields
            int id = Integer.parseInt(idtxtf.getText());
            String nom = nomtxtf.getText();
            int nbLampadaires = Integer.parseInt(nblamptxtf.getText());
            double consommationTotale = Double.parseDouble(consomtottxtf.getText());

            // Create a new Quartier object
            Quartier quartier = new Quartier(id, nom, nbLampadaires, consommationTotale);

            if (currentQuartier == null) {
                // Adding a new Quartier
                quartierService.addEntity(quartier);
                showAlert("Succès", "Le quartier a été ajouté avec succès !");
            } else {
                // Updating an existing Quartier
                quartierService.updateEntity(quartier);
                showAlert("Succès", "Le quartier a été mis à jour avec succès.");
                parentController.refreshTable(); // Refresh the table in the parent controller
            }

            // Redirect to the list of Quartiers
            redirectToListeQuartier();

        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez vérifier les valeurs saisies.");
        } catch (IOException e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    /**
     * Validates the input fields.
     *
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        if (nomtxtf.getText().isEmpty() || nblamptxtf.getText().isEmpty() || consomtottxtf.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return false;
        }

        try {
            Integer.parseInt(nblamptxtf.getText());
            Double.parseDouble(consomtottxtf.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour 'Nombre de lampadaires' et 'Consommation totale'.");
            return false;
        }

        return true;
    }

    /**
     * Redirects to the ListeQuartier view.
     *
     * @throws IOException If the FXML file cannot be loaded.
     */
    private void redirectToListeQuartier() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeQuartier.fxml"));
        Parent root = loader.load();
        ListeQuartierController listeController = loader.getController();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Displays an alert dialog.
     *
     * @param title   The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}