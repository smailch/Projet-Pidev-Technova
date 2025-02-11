package services;

import entities.DocumentAdministratif;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PDFGenerator {
    public static void generatePDF(DocumentAdministratif doc) {
        // Création du dossier "documents" s'il n'existe pas
        String directoryPath = "./";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Dossier 'documents' créé avec succès.");
            } else {
                System.err.println("Erreur lors de la création du dossier 'documents'.");
                return;
            }
        }

        // Définition du chemin du fichier PDF
        String filePath = directoryPath + File.separator + doc.getNomDocument().replaceAll("\\s+", "_") + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Document ID: " + doc.getId()));
            document.add(new Paragraph("Nom: " + doc.getNomDocument()));
            document.add(new Paragraph("Date d'émission: " + doc.getDateEmission()));
            document.add(new Paragraph("Statut: " + doc.getStatus()));
            document.add(new Paragraph("Remarque: " + doc.getRemarque()));

            document.close();
            System.out.println("✅ PDF généré avec succès: " + filePath);
        } catch (FileNotFoundException e) {
            System.err.println("❌ Erreur: Impossible de trouver ou créer le fichier PDF. Vérifiez les permissions.");
        } catch (IOException e) {
            System.err.println("❌ Erreur d'écriture du PDF: " + e.getMessage());
        }
    }
}
