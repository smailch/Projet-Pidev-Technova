package controllers;
import javafx.concurrent.Task;
import services.UtilisateurService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import entities.Utilisateur;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import services.PythonFaceService;
import tools.Myconnection;

import javax.swing.*;

public class ProfileController extends Component {

    @FXML
    private Label lblNom;

    @FXML
    private Label lblPrenom;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblRole;

    @FXML
    private Label lblDateInscription;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnDownloadPDF;

    @FXML
    public void initialize() {
        // Retrieve the user data from SharedDataController
        Utilisateur utilisateur = SharedDataController.getInstance().getUtilisateur();

        if (utilisateur != null) {
            lblNom.setText(utilisateur.getNom());
            lblPrenom.setText(utilisateur.getPrenom());
            lblEmail.setText(utilisateur.getEmail());
            lblRole.setText(utilisateur.getRole().toString());

            // Format the date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(utilisateur.getDateInscription());
            lblDateInscription.setText(formattedDate);
        } else {
            showAlert(Alert.AlertType.WARNING, "Utilisateur non trouvé", "Aucun utilisateur trouvé.");
        }
    }
    @FXML
    private void handleRetour() {
        System.out.println("Début de l'enregistrement du visage...");

        Task<String> captureTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                PythonFaceService pythonFaceService = new PythonFaceService();
                String embedding = pythonFaceService.captureAndGetFaceEmbedding();
                System.out.println("Résultat du script Python : " + embedding);
                return embedding;
            }
        };

        captureTask.setOnSucceeded(event -> {
            String faceEmbedding = captureTask.getValue();

            if (faceEmbedding == null) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la capture du visage.");
                return;
            }

            System.out.println("Embedding capturé : " + faceEmbedding);

            // Vérification utilisateur connecté
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
            } else {
                JOptionPane.showMessageDialog(null, "L'enregistrement du visage a échoué.");
                System.out.println("Échec de la mise à jour.");
            }
        });

        captureTask.setOnFailed(event -> {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement du visage.");
            captureTask.getException().printStackTrace();
        });

        new Thread(captureTask).start();
    }





    @FXML
    private void handleDownloadPDF() {
        // Retrieve the user data from SharedDataController
        Utilisateur utilisateur = SharedDataController.getInstance().getUtilisateur();

        if (utilisateur != null) {
            try (PDDocument document = new PDDocument()) {
                // Create a new page with the default page size
                PDPage page = new PDPage(); // This uses the default page size
                document.addPage(page);

                // Create a content stream to add text to the PDF
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setLeading(18f); // Set line spacing

                // Add a title centered at the top of the page without calculating the width
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                contentStream.newLineAtOffset(200, 800); // Fixed position for the title
                contentStream.showText("Attestation de Citoyenneté");
                contentStream.endText();

                // Add a horizontal line under the title (styling) closer to the title
                contentStream.setLineWidth(1f);
                contentStream.moveTo(100, 770); // Move the line closer to the title
                contentStream.lineTo(500, 770);
                contentStream.stroke();

                // Add the introductory paragraph before the user data
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 730); // Adjusted position for the paragraph

                contentStream.showText("Par la présente, nous certifions que l'utilisateur");
                contentStream.newLine();
                contentStream.showText("ci-dessous est officiellement reconnu comme citoyen");
                contentStream.newLine();
                contentStream.showText("de notre système CIVISmart. Cette attestation lui");
                contentStream.newLine();
                contentStream.showText("permet d'accéder aux services numériques et administratifs.");
                contentStream.endText();

                // Define font and positioning for user data
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                float xPosition = (page.getMediaBox().getWidth() - 400) / 2; // Center text horizontally
                float yPosition = 620; // Adjusted position for the user data below the paragraph
                contentStream.newLineAtOffset(xPosition, yPosition); // Center text on the page

                // Add the user details in a more structured format
                contentStream.showText("Nom : " + utilisateur.getNom());
                contentStream.newLine();
                contentStream.showText("Prénom : " + utilisateur.getPrenom());
                contentStream.newLine();
                contentStream.showText("Email : " + utilisateur.getEmail());
                contentStream.newLine();
                contentStream.showText("Rôle : " + utilisateur.getRole());
                contentStream.newLine();

                // Add formatted registration date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(utilisateur.getDateInscription());
                contentStream.newLine();
                contentStream.showText("Date d'inscription : " + formattedDate);

                contentStream.endText();

                // Add a footer with a line closer to the bottom
                contentStream.setLineWidth(1f);
                contentStream.moveTo(100, 120); // Move the line closer to the bottom
                contentStream.lineTo(500, 120);
                contentStream.stroke();

                // Add the image (stamp) in the bottom-right corner closer to the bottom
                try (InputStream inputStream = getClass().getResourceAsStream("/assets/images/tempon.png")) {
                    PDImageXObject stamp = PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), "tempon.png");
                    PDPageContentStream imageStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    float imageWidth = 120; // Increased stamp width
                    float imageHeight = 120; // Increased stamp height
                    float xImagePosition = page.getMediaBox().getWidth() - imageWidth - 50; // Right side
                    float yImagePosition = 110; // Move stamp closer to the bottom
                    imageStream.drawImage(stamp, xImagePosition, yImagePosition, imageWidth, imageHeight);
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Handle image loading error
                }

                // Ensure all content is written before saving
                contentStream.close();

                // Save the document
                String filePath = "Attestation_Citoyennete_" + utilisateur.getNom() + ".pdf";
                document.save(filePath);

                // Show success alert
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Attestation générée avec succès !\n\nFichier : " + filePath);

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la génération du PDF : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Utilisateur non trouvé", "Aucun utilisateur trouvé pour générer le PDF.");
        }
    }









    // Method to display alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public boolean updateUserInDatabase(Utilisateur utilisateur) {
        Connection cnx = Myconnection.getInstance().getCnx();
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

}
