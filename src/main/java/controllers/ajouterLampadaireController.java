package controllers;

import entities.Lampadaire;
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
import services.LampadaireService;
import services.QuartierService;

import java.io.IOException;

public class ajouterLampadaireController {
    @FXML
    private Button button;

    @FXML
    private TextField consomtextfield;

    @FXML
    private TextField etattextfield;

    @FXML
    private TextField idtextfield;

    @FXML
    private TextField loctextfield;

    @FXML
    private TextField nomQtextfield;

    private Lampadaire lampadaireToModify; // To store the lampadaire being modified

    // Method to set the data for modification
    public void setLampadaire(Lampadaire lampadaire) {
        this.lampadaireToModify = lampadaire;
        idtextfield.setText(String.valueOf(lampadaire.getId()));
        loctextfield.setText(lampadaire.getLocalisation());
        etattextfield.setText(lampadaire.isEtat() ? "1" : "0");
        consomtextfield.setText(String.valueOf(lampadaire.getConsommation()));
        nomQtextfield.setText(lampadaire.getQuartier().getNom());
    }

    @FXML
    void ajouterLampadaireAction(ActionEvent event) {
        try {
            // Retrieve the values from the fields
            int id = Integer.parseInt(idtextfield.getText());
            String localisation = loctextfield.getText();
            boolean etat = Integer.parseInt(etattextfield.getText()) == 1;
            double consommation = Double.parseDouble(consomtextfield.getText());
            String nomQuartier = nomQtextfield.getText();

            QuartierService quartierService = new QuartierService();
            Quartier quartier = quartierService.getQuartierByNom(nomQuartier);

            if (quartier == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Quartier introuvable");
                alert.setContentText("Le quartier '" + nomQuartier + "' n'existe pas.");
                alert.showAndWait();
                return;
            }

            Lampadaire lampadaire = new Lampadaire(id, localisation, etat, consommation, quartier);

            LampadaireService lampadaireService = new LampadaireService();

            if (lampadaireToModify == null) {
                // Adding a new Lampadaire
                lampadaireService.addEntity(lampadaire);
                showAlert("Succès", "Le lampadaire a été ajouté avec succès !");
            } else {
                // Modifying an existing Lampadaire
                lampadaire.setId(lampadaireToModify.getId());  // Preserve the ID
                lampadaireService.updateEntity(lampadaire);
                showAlert("Succès", "Le lampadaire a été modifié avec succès !");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeLampadaire.fxml"));
            Parent root = loader.load();
            ListeLampadaireController listeController = loader.getController();
            listeController.refreshTable();
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Veuillez vérifier les valeurs saisies.");
        } catch (IOException e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

