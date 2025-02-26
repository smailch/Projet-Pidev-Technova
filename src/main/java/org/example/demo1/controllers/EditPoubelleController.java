package org.example.demo1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import org.example.demo1.models.Poubelle_Intelligente;
import org.example.demo1.services.PoubelleIntelligenteService;

public class EditPoubelleController {

    @FXML
    private TextField typeDechetsField;

    @FXML
    private TextField niveauRemplissageField;

    @FXML
    private TextField localisationField;

    @FXML
    private TextField zoneIdField;

    private Poubelle_Intelligente poubelle;
    private PoubelleIntelligenteService poubelleService = new PoubelleIntelligenteService();

    private Dialog<ButtonType> dialog;

    public void setPoubelle(Poubelle_Intelligente poubelle) {
        this.poubelle = poubelle;
        populateFields();
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    private void populateFields() {
        if (poubelle != null) {
            typeDechetsField.setText(poubelle.getType_dechets());
            niveauRemplissageField.setText(String.valueOf(poubelle.getNiveau_remplissage()));
            localisationField.setText(poubelle.getLocalisation());
            zoneIdField.setText(String.valueOf(poubelle.getZoneId()));
        }
    }

    @FXML
    private void handleSave() {
        if (poubelle != null) {
            // Update the poubelle object with the new values
            poubelle.setType_dechets(typeDechetsField.getText());
            poubelle.setNiveau_remplissage(Double.parseDouble(niveauRemplissageField.getText()));
            poubelle.setLocalisation(localisationField.getText());
            poubelle.setZoneId(Integer.parseInt(zoneIdField.getText()));

            // Save the updated poubelle to the database
            poubelleService.updatePoubelle(poubelle);

            // Close the dialog
            dialog.setResult(ButtonType.OK);
        }
    }
}