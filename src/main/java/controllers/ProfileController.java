package controllers;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import controllers.SharedDataController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import entities.Utilisateur;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ProfileController {

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
    private void handleRetour(Event event) {
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        HBox titleBar = NavigationUtils.createCustomTitleBar(currentStage);
        NavigationUtils.switchPage("/gestionutilisateurs.fxml", currentStage, titleBar);
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

                // Add a horizontal line under the title (styling)
                contentStream.setLineWidth(1f);
                contentStream.moveTo(100, 790);
                contentStream.lineTo(500, 790);
                contentStream.stroke();

                // Define font and positioning for user data
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                float xPosition = (page.getMediaBox().getWidth() - 400) / 2; // Center text horizontally
                float yPosition = (page.getMediaBox().getHeight() - 200) / 2; // Center text vertically
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

                // Add a footer with a line
                contentStream.setLineWidth(1f);
                contentStream.moveTo(100, 100);
                contentStream.lineTo(500, 100);
                contentStream.stroke();

                // Add the image (stamp) in the bottom-right corner
                try (InputStream inputStream = getClass().getResourceAsStream("/assets/images/tempon.png")) {
                    PDImageXObject stamp = PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), "tempon.png");
                    PDPageContentStream imageStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    float imageWidth = 120; // Increased stamp width
                    float imageHeight = 120; // Increased stamp height
                    float xImagePosition = page.getMediaBox().getWidth() - imageWidth - 50; // Right side
                    float yImagePosition = 50; // Bottom side
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
}
