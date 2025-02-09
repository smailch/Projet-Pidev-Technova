package test;

import entities.DocumentAdministratif;
import entities.DossierFiscale;
import services.DocumentAdministratifService;
import services.DossierFiscaleService;
import services.PDFGenerator;
import entities.Lampadaire;
import entities.Quartier;
import services.LampadaireService;
import services.QuartierService;
import tools.MyConnection;

public class MainClass {
    public static void main(String[] args){
        MyConnection mc = MyConnection.getInstance();

        DossierFiscale dossier = new DossierFiscale(1, 9, 2024, 1000.0, 200.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier1 = new DossierFiscale(1, 9, 2025, 1000.0, 600.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier2 = new DossierFiscale(2, 9, 2025, 100000.0, 10.0, "En cours", "2025-02-05", "cash");

        DossierFiscaleService dossierService = new DossierFiscaleService();
        dossierService.deleteEntity(dossier1);
        dossierService.deleteEntity(dossier2);

        dossierService.addEntity(dossier);
        dossierService.addEntity(dossier2);

        dossierService.updateEntity(dossier1);
        System.out.println(dossierService.getAllData());

        DocumentAdministratif doc=new DocumentAdministratif(1,1,"Justificatif a domicile","/projet","2025-01-03","validé","pas de remarque");
        DocumentAdministratifService DocS=new DocumentAdministratifService();
        DocS.addEntity(doc);
        //dossierService.deleteEntity(dossier1);
        dossierService.ExportPDF(dossier2);
        dossierService.ExportExcel("data");


        
//        Lampadaire p=new Lampadaire(3, "Rue de Bourguiba", true, 150.3);
//        Quartier q=new Quartier(2,4,150.0);
//        LampadaireService ps=new LampadaireService();
//        QuartierService qs = new QuartierService();
//        qs.updateEntity(q);
//        //ps.addEntity(p);
//        System.out.println(ps.getAllData());
//        System.out.println(qs.getAllData());
    }
}
