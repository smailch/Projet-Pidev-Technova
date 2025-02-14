package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import services.JwtService;
import services.UtilisateurService;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tools.MyConnection;

public class LoginController implements Initializable {

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;

    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    Connection con = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    @FXML
    public void handleButtonAction(MouseEvent event) {
        if (event.getSource() == btnSignin) {
            Utilisateur utilisateur = logIn();
            if (utilisateur != null) {
                Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
                NavigationUtils.switchPage("/ForgetPassword.fxml", currentStage, titleBar);
            }
        }
    }

    @FXML
    public void redirectToForgotPassword(MouseEvent event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Create custom title bar
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);

        // Switch to the login page
        NavigationUtils.switchPage("/ForgetPassword.fxml", currentStage, titleBar);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateurService = new UtilisateurService();
        jwtService = new JwtService();
        con = MyConnection.getInstance().getCnx();

        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up : Good to go");
        }
    }

    private Utilisateur logIn() {
        String email = txtUsername.getText();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            setLblError(Color.TOMATO, "⚠️ Email et mot de passe requis !");
            return null;
        }

        Utilisateur utilisateur = utilisateurService.connexion(email, password);

        if (utilisateur == null) {
            setLblError(Color.TOMATO, "❌ Email ou mot de passe incorrect.");
        } else {
            setLblError(Color.GREEN, "✅ Connexion réussie !");
        }

        return utilisateur;
    }

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
        System.out.println(text);
    }
    @FXML
    public void redirectToSignUp(MouseEvent event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Create custom title bar
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);

        // Switch to the login page
        NavigationUtils.switchPage("/SignUp.fxml", currentStage, titleBar);
    }


}
