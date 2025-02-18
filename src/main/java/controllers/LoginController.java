package controllers;

import entities.Utilisateur;
import javafx.event.ActionEvent;
import services.*;
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
import tools.Myconnection;

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
                try {
                    SharedDataController.getInstance().setUtilisateur(utilisateur);

                    System.out.println("✅ Connexion réussie : " + utilisateur.getNom());

                    // Générer le JWT
                    String jwtToken = jwtService.generateToken(utilisateur);
                    System.out.println("🔑 Token JWT : " + jwtToken);

                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
                    Scene scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    System.err.println("Error loading the scene: " + ex.getMessage());
                    setLblError(Color.TOMATO, "Failed to load next screen");
                }
            }
        }
    }

    @FXML
    public void redirectToForgotPassword(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
            Scene scene = new Scene(loader.load());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Mot de passe oublié");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setLblError(Color.TOMATO, "Erreur lors du chargement de l'écran de réinitialisation du mot de passe.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        utilisateurService = new UtilisateurService();
        jwtService = new JwtService();
        con = Myconnection.getInstance().getCnx();

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
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUp.fxml"));
            Scene scene = new Scene(loader.load());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("SignUp");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setLblError(Color.TOMATO, "Erreur lors du chargement de l'écran de réinitialisation du SignUp.");
        }
    }


}
