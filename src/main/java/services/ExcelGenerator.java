package services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import entities.DocumentAdministratif;
import services.DocumentAdministratifService;

public class ExcelGenerator {
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
