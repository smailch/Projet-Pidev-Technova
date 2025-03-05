package services;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import com.itextpdf.layout.element.LineSeparator;
public class ProfessionalPDFGenerator {

    /**
     * Generates a professional PDF based on user input and sends it via email.
     * @param userInput The text from the JavaFX TextArea.
     * @return The generated PDF file path.
     */
    public static String generatePdf(String userInput) {
        try {
            // Extract Data
            HashMap<String, String> data = extractData(userInput);

            // Get current date for file name
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String title = data.getOrDefault("Title", "Document");
            String fileName = title.replace(" ", "_") + "_" + currentDate + ".pdf";

            // Define file path
            String filePath = "C:/Generated_PDFs/" + fileName;
            Files.createDirectories(Paths.get("C:/Generated_PDFs/"));

            // Create PDF Document
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // PDF Styling
            document.add(new Paragraph(title)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
            );

            // Add extracted data
            addParagraph(document, "Nom: ", data.getOrDefault("Nom", "Non spécifié"));
            addParagraph(document, "Prénom: ", data.getOrDefault("Prenom", "Non spécifié"));
            addParagraph(document, "Date de Naissance: ", data.getOrDefault("Date de Naissance", "Non spécifiée"));
            addParagraph(document, "Nationalité: ", data.getOrDefault("Nationalité", "Non spécifiée"));


            addParagraph(document, "Description: ", data.getOrDefault("Description", "Aucune description fournie."));

            document.add(new Paragraph("\n\nSignature: ___________________________")
                    .setTextAlignment(TextAlignment.RIGHT)
            );

            document.close();
            System.out.println("PDF Generated: " + filePath);
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("File not found!");

            }

            try {
                // Check if the Desktop class is supported
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(file); // Opens the file with the default PDF viewer
                } else {
                    System.out.println("Desktop is not supported.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts structured data from user input.
     * @param userInput The raw user input string.
     * @return A HashMap containing extracted key-value pairs.
     */
    private static HashMap<String, String> extractData(String userInput) {
        HashMap<String, String> data = new HashMap<>();
        String[] lines = userInput.split("\n");

        Pattern pattern = Pattern.compile("^(Title|Nom|Prenom|Date de Naissance|Nationalité|Description):\\s*(.*)$");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                data.put(matcher.group(1), matcher.group(2).trim());
            }
        }

        return data;
    }

    /**
     * Adds formatted paragraph to the PDF document.
     */
    private static void addParagraph(Document document, String label, String value) {
        Text labelText = new Text(label).setBold();
        Text valueText = new Text(value);

        document.add(new Paragraph().add(labelText).add(valueText));
    }

}
