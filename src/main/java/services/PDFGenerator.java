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
import entities.DossierFiscale;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class PDFGenerator {
    public static void GeneratePDF(DossierFiscale dossier){
        String path="dossier_"+dossier.getId()+".pdf";
        try {
            DossierFiscaleService df=new DossierFiscaleService();
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
            table.addCell(new Cell().add("Dossier Fiscale").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
            Table nestedtab=new Table(new float[]{twocol/2,twocol150/2});

            nestedtab.addCell(getHeaderTextCell("Dossier No:"));
            nestedtab.addCell(getHeaderTextCellValue(String.valueOf(dossier.getId())));

            nestedtab.addCell(getHeaderTextCell("Date creation:"));
            nestedtab.addCell(getHeaderTextCellValue(dossier.getDateCreation()));

            table.addCell(new Cell().add(nestedtab).setBorder(Border.NO_BORDER));


            Border gb=new SolidBorder(Color.GRAY,1f/2f);
            Table divider=new Table(fullwidth);



            divider.setBorder(gb);
            doc.add(table);
            doc.add(onesp);
            doc.add(divider);
            doc.add(onesp);


            Table table2=new Table(twocolumnWidth);
            table2.addCell(getInformationsCell("Informations du client"));
            table2.addCell(getInformationsCell("Informations du dossier"));

            Table table3=new Table(twocolumnWidth);
            table3.addCell(getCell10fLeft("Nom",true));
            table3.addCell(getCell10fLeft("Annee Fiscale",true));
            table3.addCell(getCell10fLeft("Chaabane",false));
            table3.addCell(getCell10fLeft(String.valueOf(dossier.getAnneeFiscale()),false));

            Table table4=new Table(twocolumnWidth);
            table4.addCell(getCell10fLeft("Prenom",true));
            table4.addCell(getCell10fLeft("Status du dossier",true));
            table4.addCell(getCell10fLeft("Ismail",false));
            table4.addCell(getCell10fLeft(String.valueOf(dossier.getStatus()),false));

            Table table5=new Table(twocolumnWidth);
            table5.addCell(getCell10fLeft("Adresse",true));
            table5.addCell(getCell10fLeft("Moyen du paiment",true));
            table5.addCell(getCell10fLeft("27 rue Egypte Korba Nabeul Tunisie 8070",false));
            table5.addCell(getCell10fLeft(String.valueOf(dossier.getMoyenPaiement()),false));

            Table table6=new Table(twocolumnWidth);
            table6.addCell(getCell10fLeft("Email",true));
            table6.addCell(getCell10fLeft("",true));
            table6.addCell(getCell10fLeft("ichaabane6@gmail.com",false));
            table6.addCell(getCell10fLeft("",false));




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

            threeColTable1.addCell(new Cell().add("Description").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER));
            threeColTable1.addCell(new Cell().add("Impot totale").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            threeColTable1.addCell(new Cell().add("Impot paye").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);
            doc.add(threeColTable1);

            Table threeColTable2=new Table(threeColumnWidth);
            threeColTable2.addCell(new Cell().add("Informations impot").setBorder(Border.NO_BORDER));
            threeColTable2.addCell(new Cell().add(String.valueOf(dossier.getTotalImpot())).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            threeColTable2.addCell(new Cell().add(String.valueOf(dossier.getTotalImpotPaye())).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);
            doc.add(threeColTable2.setMarginBottom(20f));

            float[] onetwo ={threecol+125f,threecol*2};
            Table threeColTable4=new Table(onetwo);
            threeColTable4.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
            threeColTable4.addCell(new Cell().add(fullwidthDashedBorder(fullwidth)).setBorder(Border.NO_BORDER));
            doc.add(threeColTable4);


            Table threeColTable3=new Table(threeColumnWidth);
            threeColTable3.addCell(new Cell().add("").setBorder(Border.NO_BORDER)).setMarginLeft(10f);
            threeColTable3.addCell(new Cell().add("Total").setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
            threeColTable3.addCell(new Cell().add(String.valueOf(df.getResteImpot(dossier))).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);
            doc.add(threeColTable3);
            doc.add(divider2.setBorder(dbg).setMarginBottom(30f));

            Table tablesig=new Table(twocolumnWidth);
            tablesig.addCell(getCell10fLeft("",true));
            tablesig.addCell(getCell10fLeft("Signature du Client",true).setFontSize(15f));
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

    static  Cell getCell10fLeft(String textValue,Boolean isBold){
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




}
