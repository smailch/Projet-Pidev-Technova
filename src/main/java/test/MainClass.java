package test;

import entities.DocumentAdministratif;
import entities.DossierFiscale;
import services.DocumentAdministratifService;
import services.DossierFiscaleService;
import services.PDFGenerator;
import tools.MyConnection;

public class MainClass {
    public static void main(String[] args){
        MyConnection mc = MyConnection.getInstance();

        DossierFiscale dossier = new DossierFiscale(1, 1, 2024, 1000.0, 200.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier1 = new DossierFiscale(1, 1, 2025, 1000.0, 600.0, "En cours", "2025-02-05", "Carte bancaire");

        DossierFiscaleService dossierService = new DossierFiscaleService();
        dossierService.deleteEntity(dossier1);

        dossierService.addEntity(dossier);

        System.out.println(dossierService.getAllData());
        dossierService.updateEntity(dossier1);
        System.out.println(dossierService.getAllData());

        DocumentAdministratif doc=new DocumentAdministratif(1,1,"Justificatif a domicile","/projet","2025-01-03","validé","pas de remarque");
        DocumentAdministratifService DocS=new DocumentAdministratifService();
        DocS.addEntity(doc);
        //dossierService.deleteEntity(dossier1);
        dossierService.ExportPDF(dossier);
    }
}
