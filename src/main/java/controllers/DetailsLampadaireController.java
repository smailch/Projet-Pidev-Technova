package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DetailsLampadaireController {

    @FXML
    private TextField lbconsommation;

    @FXML
    private TextField lbetat;

    @FXML
    private TextField lbid;

    @FXML
    private TextField lblocalisation;

    @FXML
    private TextField lbnomq;

    public void setLbId(String id) {
        this.lbid.setText(id);
    }

    public void setLbConsommation(String consommation) {
        this.lbconsommation.setText(consommation);
    }

    public void setLbEtat(String etat) {
        this.lbetat.setText(etat);
    }

    public void setLblocalisation(String localisation) {
        this.lblocalisation.setText(localisation);
    }

    public void setLbNomQ(String nomQuartier) {
        this.lbnomq.setText(nomQuartier);
    }
}
