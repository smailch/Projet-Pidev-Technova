package com.example.demo.utils;

import com.example.demo.models.Lampadaire;
import com.example.demo.models.Quartier;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    private static final int MARGIN_X = 50;
    private static final int MARGIN_Y = 700;
    private static final int ROW_HEIGHT = 20;
    private static final int CELL_PADDING = 5;

    public static void generateQuartierPDF(List<Quartier> quartiers, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up the title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.setNonStrokingColor(new Color(0, 102, 204)); // Smart city theme color
                drawText(contentStream, "List of Quartiers", MARGIN_X, MARGIN_Y);

                // Set up the table headers
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                int y = MARGIN_Y - 30;
                drawTableRow(contentStream, new String[]{"ID", "Name", "Lamps", "Consumption"}, MARGIN_X, y);
                y -= ROW_HEIGHT;

                // Fill the table with data
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setNonStrokingColor(Color.BLACK);
                for (Quartier quartier : quartiers) {
                    drawTableRow(contentStream, new String[]{
                            String.valueOf(quartier.getId()),
                            quartier.getNom(),
                            String.valueOf(quartier.getNbLamp()),
                            String.valueOf(quartier.getConsomTot())
                    }, MARGIN_X, y);
                    y -= ROW_HEIGHT;
                }
            }

            document.save(filePath);
        }
    }

    public static void generateLampadairePDF(List<Lampadaire> lampadaires, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set up the title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.setNonStrokingColor(new Color(0, 102, 204)); // Smart city theme color
                drawText(contentStream, "List of Lampadaires", MARGIN_X, MARGIN_Y);

                // Set up the table headers
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                int y = MARGIN_Y - 30;
                drawTableRow(contentStream, new String[]{"ID", "Location", "State", "Consumption", "Installation Date"}, MARGIN_X, y);
                y -= ROW_HEIGHT;

                // Fill the table with data
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setNonStrokingColor(Color.BLACK);
                for (Lampadaire lampadaire : lampadaires) {
                    drawTableRow(contentStream, new String[]{
                            String.valueOf(lampadaire.getId()),
                            lampadaire.getLocalisation(),
                            lampadaire.isEtat() ? "On" : "Off",
                            String.valueOf(lampadaire.getConsommation()),
                            lampadaire.getDate_installation().toString()
                    }, MARGIN_X, y);
                    y -= ROW_HEIGHT;
                }
            }

            document.save(filePath);
        }
    }

    private static void drawTableRow(PDPageContentStream contentStream, String[] data, int x, int y) throws IOException {
        for (int i = 0; i < data.length; i++) {
            drawText(contentStream, data[i], x + i * 100, y);
        }
    }

    private static void drawText(PDPageContentStream contentStream, String text, int x, int y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}