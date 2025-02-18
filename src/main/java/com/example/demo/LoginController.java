package com.example.demo;

import com.example.demo.entities.Utilisateur;
import com.example.demo.services.SessionManager;
import javafx.event.ActionEvent;
import com.example.demo.services.JwtService;
import com.example.demo.services.UtilisateurService;
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
import com.example.demo.tools.MyConnection;
import javafx.scene.Parent;
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
            Utilisateur utilisateur = logIn(); // Connexion de l'utilisateur
            if (utilisateur != null) {
                try {
                    // Affichage du rôle de l'utilisateur pour le débogage
                    System.out.println("✅ Connexion réussie : " + utilisateur.getNom() + " " + utilisateur.getRole());

                    // Générer le JWT et sauvegarder l'utilisateur connecté
                    SessionManager.getInstance().setUtilisateurConnecte(utilisateur);

                    // Fermer la fenêtre de connexion actuelle
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                    FXMLLoader loader;
                    Scene scene;

                    // Vérifier le rôle de l'utilisateur et charger la page correspondante
                    String role = String.valueOf(utilisateur.getRole());  // Rendre le rôle insensible à la casse
                    if (role.equals("Citoyen")) {
                        // Si l'utilisateur est un citoyen, naviguer vers la page ajouter incident
                        loader = new FXMLLoader(getClass().getResource("/ajouterincidentForm.fxml"));
                    } else if (role.equals("Admin")) {
                        // Si l'utilisateur est un administrateur, naviguer vers la page admin
                        loader = new FXMLLoader(getClass().getResource("/adminincidentForm.fxml"));
                    } else {
                        // Si le rôle de l'utilisateur n'est ni admin ni citoyen, afficher un message d'erreur
                        setLblError(Color.TOMATO, "Rôle inconnu");
                        return; // Sortir de la méthode si le rôle est invalide
                    }

                    // Charger la scène correspondante et l'afficher
                    scene = new Scene(loader.load());
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    System.err.println("Erreur lors du chargement de la scène : " + ex.getMessage());
                    setLblError(Color.TOMATO, "Échec du chargement de la page suivante");
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
        con = MyConnection.getInstance().getConnection();

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
            System.out.println("Le fichier FXML s'est chargé avec succès !");

            Scene scene;
            scene = new Scene(loader.load());
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Mot de passe oublié");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            setLblError(Color.TOMATO, "Erreur lors du chargement de l'écran de réinitialisation du mot de passe.");
        }
    }


}
