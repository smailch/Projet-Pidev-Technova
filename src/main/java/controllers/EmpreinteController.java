package controllers;

import entities.Utilisateur;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PythonFaceService;
import tools.MyConnection;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static controllers.SignUpController.showAlert;

public class EmpreinteController {
    private static final Logger log = LoggerFactory.getLogger(EmpreinteController.class);
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnPasse;


    @FXML
    private void Enregistre_Visage() {
        btnPasse.setDisable(true);
        System.out.println("Début de l'enregistrement du visage...");

        Task<String> captureTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                PythonFaceService pythonFaceService = new PythonFaceService();
                String embedding = null;
                while (embedding == null || embedding.equals("Aucun visage détecté")) {
                    embedding = pythonFaceService.captureAndGetFaceEmbedding();
                    log.info("Résultat du script Python : {}", embedding);
                }
                return embedding;
            }
        };

        captureTask.setOnSucceeded(event -> {
            String faceEmbedding = captureTask.getValue();

            if (faceEmbedding == "Aucun visage détecté") {
                JOptionPane.showMessageDialog(null, "Erreur lors de la capture du visage.");
                return;
            }

            System.out.println("Embedding capturé : " + faceEmbedding);

            // Vérification
            SharedDataController sharedData = SharedDataController.getInstance();
            Utilisateur utilisateur = sharedData.getUtilisateur();

            if (utilisateur == null) {
                JOptionPane.showMessageDialog(null, "Aucun utilisateur connecté.");
                return;
            }

            System.out.println("Utilisateur récupéré : " + utilisateur.getEmail());

            // Mettre à jour l'utilisateur avec l'encodage du visage
            utilisateur.setVisageHash(faceEmbedding);

            // Enregistrer dans la base de données
            boolean updated = updateUserInDatabase(utilisateur);

            if (updated) {
                JOptionPane.showMessageDialog(null, "Visage bien enregistré !");
                System.out.println("Mise à jour réussie !");
                redirectToLogin();
            } else {
                JOptionPane.showMessageDialog(null, "L'enregistrement du visage a échoué.");
                System.out.println("Échec de la mise à jour.");
            }
        });

        captureTask.setOnFailed(event -> {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement du visage.");
            captureTask.getException().printStackTrace();
            btnPasse.setDisable(false);
        });

        new Thread(captureTask).start();
    }


    public boolean updateUserInDatabase(Utilisateur utilisateur) {
        Connection cnx = MyConnection.getInstance().getCnx();
        String query = "UPDATE Utilisateur SET visage_hash = ? WHERE Email = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, utilisateur.getVisageHash());
            pstmt.setString(2, utilisateur.getEmail());

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Nombre de lignes mises à jour : " + rowsUpdated);

            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return false;
    }


    @FXML
    private void redirectToLogin() {
        try {
            // Charger la nouvelle fenêtre Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle (celle de l'enregistrement de visage)
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void Passe() {
        redirectToLogin();
    }




}
