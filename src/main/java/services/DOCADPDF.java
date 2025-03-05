package services;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import entities.DocumentAdministratif;
import entities.DossierFiscale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DOCADPDF {
    public static void GeneratePDFDoc(DocumentAdministratif dossier){
        String path="dossier_"+dossier.getId()+"_"+dossier.getDateEmission()+".pdf";
        try {
            DocumentAdministratifService df=new DocumentAdministratifService();
            PdfWriter pdf=new PdfWriter(path);
            PdfDocument pdfdoc=new PdfDocument(pdf);
            pdfdoc.setDefaultPageSize(PageSize.A4);
            Document doc=new Document(pdfdoc);
            float twocol=285f;
            float twocol150=twocol+150f;
            float threecol=190f;
            float[] twocolumnWidth ={twocol150,twocol};
            float[] fullwidth ={threecol*3};
            float[] threeColumnWidth ={threecol,threecol,threecol};


            Paragraph onesp=new Paragraph("\n");


            Table table=new Table(twocolumnWidth);
            table.addCell(new Cell().add("document Administratif").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
            Table nestedtab=new Table(new float[]{twocol/2,twocol150/2});

            nestedtab.addCell(getHeaderTextCell("document No:"));
            nestedtab.addCell(getHeaderTextCellValue(String.valueOf(dossier.getId())));

            nestedtab.addCell(getHeaderTextCell("Date Emission:"));
            nestedtab.addCell(getHeaderTextCellValue(dossier.getDateEmission().toString()));

            table.addCell(new Cell().add(nestedtab).setBorder(Border.NO_BORDER));


            Border gb=new SolidBorder(Color.GRAY,1f/2f);
            Table divider=new Table(fullwidth);



            divider.setBorder(gb);
            doc.add(table);
            doc.add(onesp);
            doc.add(divider);
            doc.add(onesp);


            Table table2=new Table(twocolumnWidth);
            table2.addCell(getInformationsCell("Informations du document"));

            Table table3=new Table(twocolumnWidth);

            table3.addCell(getCell10fLeft("Annee Emission",true));

            table3.addCell(getCell10fLeft(String.valueOf(dossier.getDateEmission()),false));

            Table table4=new Table(twocolumnWidth);

            table4.addCell(getCell10fLeft("Status du document",true));

            table4.addCell(getCell10fLeft(String.valueOf(dossier.getStatus()),false));

            Table table5=new Table(twocolumnWidth);

            table5.addCell(getCell10fLeft("Nom de document",true));
            table5.addCell(getCell10fLeft(String.valueOf(dossier.getNomDocument()),false));

            Table table6=new Table(twocolumnWidth);

            table6.addCell(getCell10fLeft("Remarque",true));

            table6.addCell(getCell10fLeft(String.valueOf(dossier.getRemarque()),false));




            Table divider2=new Table(fullwidth);
            Border dbg=new DashedBorder(Color.GRAY,0.5f);







            doc.add(table2.setMarginBottom(12f));
            doc.add(table3.setMarginBottom(12f));
            doc.add(table4.setMarginBottom(12f));
            doc.add(table5.setMarginBottom(12f));
            doc.add(table6.setMarginBottom(12f));
            doc.add(divider2.setBorder(dbg).setMarginBottom(12f));
            Table threeColTable1=new Table(threeColumnWidth);

            threeColTable1.setBackgroundColor(Color.BLACK,0.7f);

            threeColTable1.addCell(new Cell().add("chemin").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER));

            doc.add(threeColTable1);

            Table threeColTable2=new Table(threeColumnWidth);

            threeColTable2.addCell(new Cell().add(String.valueOf(dossier.getCheminFichier())).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            doc.add(threeColTable2.setMarginBottom(20f));






            Table tablesig=new Table(twocolumnWidth);
            tablesig.addCell(getCell10fLeft("",true));
            tablesig.addCell(getCell10fLeft("Signature d'admin",true).setFontSize(15f));
            tablesig.addCell(getCell10fLeft("",true));
            tablesig.addCell(getCell10fLeft("",false));

            doc.add(tablesig.setMarginBottom(50f));

            Table tb=new Table(fullwidth);
            tb.addCell(new Cell().add("TERMES ET CONDITIONS \n").setBold().setBorder(Border.NO_BORDER));
            List<String> tnc = getTerms();

            for(String t:tnc){
                tb.addCell(new Cell().add(t).setBorder(Border.NO_BORDER));

            }
            doc.add(tb);






            doc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private static List<String> getTerms() {
        List<String> tnc=new ArrayList<>();
        tnc.add("1-Confidentialité – Ce document est strictement confidentiel et destiné uniquement au destinataire. Il ne doit pas être partagé ni divulgué.");
        tnc.add("2-Exactitude des informations – Le destinataire doit vérifier ses informations et signaler toute erreur immédiatement.");
        tnc.add("3-Utilisation des informations – Ce document est informatif et ne constitue pas un conseil juridique ou financier.");
        tnc.add("4-Limitation de responsabilité – L’émetteur n’est pas responsable des erreurs, omissions ou mauvaise utilisation des informations.");
        return tnc;
    }


    static Cell getHeaderTextCell(String textValue)
    {
        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }
    static Cell getHeaderTextCellValue(String textValue)
    {
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }
    static Cell getInformationsCell(String textValue)
    {

        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static  Cell getCell10fLeft(String textValue,boolean isBold){
        Cell myCell=new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return  isBold ?myCell.setBold():myCell;

    }
    static Table fullwidthDashedBorder(float[] fullwidth)
    {
        Table tableDivider2=new Table(fullwidth);
        Border dgb=new DashedBorder(Color.GRAY,0.5f);
        tableDivider2.setBorder(dgb);
        return tableDivider2;
    }


    public static void generatePDF(DocumentAdministratif doc) {
        // Création du dossier "documents" s'il n'existe pas
        String directoryPath = "./";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("documents créé avec succès.");
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
