package services;

import entities.DocumentAdministratif;
import entities.DossierFiscale;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static void exportDossiersToExcel(List<DossierFiscale> dossiers, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dossiers Fiscaux");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "User ID", "Année Fiscale", "Total Impôt", "Total Payé", "Statut", "Date Limite", "Moyen de Paiement"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // Populate rows with data
        int rowNum = 1;
        for (DossierFiscale dossier : dossiers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dossier.getId());
            row.createCell(1).setCellValue(dossier.getIdUser());
            row.createCell(2).setCellValue(dossier.getAnneeFiscale());
            row.createCell(3).setCellValue(dossier.getTotalImpot());
            row.createCell(4).setCellValue(dossier.getTotalImpotPaye());
            row.createCell(5).setCellValue(dossier.getStatus());
            row.createCell(6).setCellValue(dossier.getDateCreation());
            row.createCell(7).setCellValue(dossier.getMoyenPaiement());
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath+".xlsx")) {
            workbook.write(fileOut);
            System.out.println("Exportation réussie: " + filePath);
        } catch (IOException e) {
            System.out.println("Erreur d'exportation: " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void exportToExcel(String fileName) {
        DocumentAdministratifService service = new DocumentAdministratifService();
        List<DocumentAdministratif> documents = service.getAllData();

        String filePath = fileName + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Documents");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Date", "Statut", "Remarque"};
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (DocumentAdministratif doc : documents) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(doc.getId());
                row.createCell(1).setCellValue(doc.getNomDocument());
                row.createCell(2).setCellValue(doc.getDateEmission());
                row.createCell(3).setCellValue(doc.getStatus());
                row.createCell(4).setCellValue(doc.getRemarque());
            }

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();

            System.out.println("Excel généré: " + filePath);
        } catch (IOException e) {
            System.err.println("Erreur d'exportation Excel: " + e.getMessage());
        }
    }
}
