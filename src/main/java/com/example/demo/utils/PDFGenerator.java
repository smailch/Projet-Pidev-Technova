package com.example.demo.utils;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PDFGenerator {

    private static final int MARGIN_X = 50;
    private static final int MARGIN_Y = 700;
    private static final int ROW_HEIGHT = 20;
    private static final int CELL_PADDING = 5;
    private static final int TABLE_WIDTH = 500;

    // Path of the logo in resources
    private static final String LOGO_PATH = "/com/example/demo/logo.png";

    public static void generateQuartierPDF(List<Quartier> quartiers, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addLogo(document, contentStream);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                contentStream.setNonStrokingColor(new Color(0, 102, 204));
                drawText(contentStream, "Liste des Quartiers", MARGIN_X, MARGIN_Y + 20);

                int y = MARGIN_Y;
                String[] headers = {"ID", "Nom", "Lampadaires", "Consommation"};
                float[] columnWidths = {50, 200, 100, 100};
                drawTable(contentStream, headers, quartiers, y, columnWidths);
            }
            document.save(filePath);
        }
    }

    public static void generateLampadairePDF(List<Lampadaire> lampadaires, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addLogo(document, contentStream);
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                contentStream.setNonStrokingColor(new Color(0, 102, 204));
                drawText(contentStream, "Liste des Lampadaires", MARGIN_X, MARGIN_Y + 20);

                int y = MARGIN_Y;
                String[] headers = {"ID", "Localisation", "État", "Consommation", "Date d'installation"};
                float[] columnWidths = {50, 150, 100, 100, 100};
                drawTable(contentStream, headers, lampadaires, y, columnWidths);
            }
            document.save(filePath);
        }
    }

    private static void addLogo(PDDocument document, PDPageContentStream contentStream) throws IOException {
        try {
            // Try multiple ways to load the resource
            InputStream imageStream = PDFGenerator.class.getResourceAsStream(LOGO_PATH);

            if (imageStream == null) {
                // Try with classloader if direct resource access fails
                imageStream = PDFGenerator.class.getClassLoader().getResourceAsStream(LOGO_PATH);
            }

            if (imageStream == null) {
                // Try without the leading slash
                imageStream = PDFGenerator.class.getClassLoader().getResourceAsStream(LOGO_PATH.substring(1));
            }

            if (imageStream == null) {
                System.err.println("❌ Erreur : Impossible de trouver " + LOGO_PATH);
                return;
            }

            // Create PDImageXObject directly from the input stream
            PDImageXObject logo = PDImageXObject.createFromByteArray(document,
                    imageStream.readAllBytes(), "logo");

            float scale = 0.3f;
            float imgWidth = logo.getWidth() * scale;
            float imgHeight = logo.getHeight() * scale;
            float centerX = (PDRectangle.A4.getWidth() - imgWidth) / 2;
            float topY = PDRectangle.A4.getHeight() - 80;

            contentStream.drawImage(logo, centerX, topY, imgWidth, imgHeight);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void drawTable(PDPageContentStream contentStream, String[] headers, List<?> items, int startY, float[] colWidths) throws IOException {
        int y = startY - 30;
        float x = MARGIN_X;
        contentStream.setStrokingColor(Color.BLACK);

        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        for (int i = 0; i < headers.length; i++) {
            drawCell(contentStream, headers[i], x, y, colWidths[i], true);
            x += colWidths[i];
        }
        y -= ROW_HEIGHT;
        x = MARGIN_X;

        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        for (Object item : items) {
            String[] rowData = extractRowData(item);
            for (int i = 0; i < rowData.length; i++) {
                drawCell(contentStream, rowData[i], x, y, colWidths[i], false);
                x += colWidths[i];
            }
            y -= ROW_HEIGHT;
            x = MARGIN_X;
        }
    }

    private static void drawCell(PDPageContentStream contentStream, String text, float x, float y, float width, boolean isHeader) throws IOException {
        contentStream.setNonStrokingColor(isHeader ? new Color(200, 200, 200) : Color.WHITE);
        contentStream.addRect(x, y, width, -ROW_HEIGHT);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.beginText();
        contentStream.newLineAtOffset(x + CELL_PADDING, y - 15);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.lineTo(x + width, y - ROW_HEIGHT);
        contentStream.lineTo(x, y - ROW_HEIGHT);
        contentStream.closeAndStroke();
    }

    private static String[] extractRowData(Object item) {
        if (item instanceof Quartier) {
            Quartier q = (Quartier) item;
            return new String[]{
                    String.valueOf(q.getId()),
                    q.getNom(),
                    String.valueOf(q.getNbLamp()),
                    String.valueOf(q.getConsomTot())
            };
        } else if (item instanceof Lampadaire) {
            Lampadaire l = (Lampadaire) item;
            return new String[]{
                    String.valueOf(l.getId()),
                    l.getLocalisation(),
                    l.isEtat() ? "Allumé" : "Éteint",
                    String.valueOf(l.getConsommation()),
                    l.getDate_installation().toString()
            };
        }
        return new String[]{};
    }

    private static void drawText(PDPageContentStream contentStream, String text, int x, int y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}