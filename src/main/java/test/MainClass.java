package test;

import entities.*;
import javafx.fxml.FXMLLoader;
import services.*;
import entities.Lampadaire;
import entities.Quartier;
import services.LampadaireService;
import services.QuartierService;
import tools.MyConnection;
import java.util.List;

public class MainClass {
    public static void main(String[] args){
        //FXMLLoader loader=new FXMLLoader(ge)

        //declarations des entites
        DossierFiscale dossier = new DossierFiscale(1, 13, 2024, 1000.0, 200.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier1 = new DossierFiscale(1, 14, 2025, 1000.0, 600.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier2 = new DossierFiscale(2, 13, 2025, 100000.0, 10.0, "En cours", "2025-02-05", "cash");
        DossierFiscale dossier3 = new DossierFiscale(3, 13, 2025, 100000.0, 10.0, "En cours", "2025-02-05", "cash");
        DeclarationRevenues dr= new DeclarationRevenues(1,1,200.0,"esprit","2025-06-04","/fichier");
        DeclarationRevenues dr1= new DeclarationRevenues(1,1,500000,"saraya","2025-06-04","/fichier");

        //declarations des services
        DossierFiscaleService dossierService = new DossierFiscaleService();
        DeclarationRevenuesService declarationS=new DeclarationRevenuesService();


        //delete 1
        dossierService.deleteEntity(dossier1);
        dossierService.deleteEntity(dossier2);
        declarationS.deleteEntity(dr);


        //ajout
        dossierService.addEntity(dossier);
        dossierService.addEntity(dossier2);
        declarationS.addEntity(dr);

        //mise a jour
        dossierService.updateEntity(dossier1);
        declarationS.updateEntity(dr1);

        //affichage
        System.out.println(dossierService.getAllData());
        System.out.println(declarationS.getAllData());



        //exporter
        dossierService.ExportPDF(dossier3);
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
