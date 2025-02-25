package test;

import entities.*;
import services.*;
import entities.AssistantDocumentaire;
import entities.DocumentAdministratif;
import entities.Validation;
import services.AssistantDocumentaireService;
import services.DocumentAdministratifService;
import services.ValidationService;
import entities.Utilisateur;
import entities.Role;
import services.UtilisateurService;
import tools.MyConnection;
import java.util.Calendar;

public class MainClass {
    public static void main(String[] args){
        //ajout d'utilisateur
        Calendar calendar = Calendar.getInstance();
        java.sql.Date sqlDate = new java.sql.Date(calendar.getTimeInMillis());
        UtilisateurService utilisateurService = new UtilisateurService();
        Utilisateur utilisateur=new Utilisateur();
        utilisateurService.addEntity(utilisateur);

        // Ajouter un utilisateur avec la méthode addEntity (utilisant PreparedStatement)
        utilisateurService.addEntity(utilisateur);        //declarations des entites
        DossierFiscale dossier = new DossierFiscale(1, 15, 2024, 1000.0, 200.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier1 = new DossierFiscale(1, 16, 2025, 1000.0, 600.0, "En cours", "2025-02-05", "Carte bancaire");
        DossierFiscale dossier2 = new DossierFiscale(2, 15, 2025, 100000.0, 10.0, "En cours", "2025-02-05", "cash");
        DossierFiscale dossier3 = new DossierFiscale(3, 16, 2025, 100000.0, 10.0, "En cours", "2025-02-05", "cash");
        DeclarationRevenues dr= new DeclarationRevenues(1,1,200.0,"esprit","2025-06-04","/fichier");
        DeclarationRevenues dr1= new DeclarationRevenues(1,1,500000,"saraya","2025-06-04","/fichier");
        // 🗑️ Suppression d'un document
        // Document Administratif

        DocumentAdministratifService docService = new DocumentAdministratifService();

        // 🔹 Service de gestion des validations
        ValidationService validationService = new ValidationService();

        // 🔹 Service de gestion de l'Assistant Documentaire
        AssistantDocumentaireService assistantService = new AssistantDocumentaireService();

        // 📌 Création de documents administratifs
        DocumentAdministratif doc1 = new DocumentAdministratif(1,  "Justificatif de domicile", "/projet/docs",  "Validé", "Aucune remarque");
        DocumentAdministratif doc2 = new DocumentAdministratif(2, "Permis de travail", "/projet/docs",  "En attente", "Vérification en cours");

        // ✅ Ajout des documents
        docService.addEntity(doc1);
        docService.addEntity(doc2);

        // 📄 Affichage des documents
        System.out.println("📄 Documents Administratifs:");
        System.out.println(docService.getAllData());

        // 🔄 Mise à jour d'un document
        doc2.setStatus("Approuvé");
        docService.updateEntity(doc2);
        docService.deleteEntity(doc1);

        //declarations des services
        DossierFiscaleService dossierService = new DossierFiscaleService();
        DeclarationRevenuesService declarationS=new DeclarationRevenuesService();
        // 📄 Affichage après suppression
        System.out.println("📄 Documents après suppression:");
        System.out.println(docService.getAllData());

        // 📌 Création d'une validation
        Validation validation1 = new Validation(1, doc2.getId(), "Validé", "12/06/2018", "conforme");
        Validation validation2 = new Validation(2, doc2.getId(), "Rejeté", "27/07/2002", "non conforme");

        //delete 1
        dossierService.deleteEntity(dossier1);
        dossierService.deleteEntity(dossier2);
        declarationS.deleteEntity(dr);
        // ✅ Ajout des validations
        validationService.addEntity(validation1);
        validationService.addEntity(validation2);

        // 🔄 Mise à jour d'une validation
        validation2.setStatut("Validé");
        validation2.setDateValidation("2025-02-13"); // Mise à jour de la date
        validationService.updateEntity(validation2);

        //ajout
        dossierService.addEntity(dossier);
        dossierService.addEntity(dossier2);
        declarationS.addEntity(dr);
        // 🗑️ Suppression d'une validation
        validationService.deleteEntity(validation1);

        //mise a jour
        dossierService.updateEntity(dossier1);
        declarationS.updateEntity(dr1);
        // 📄 Affichage des validations restantes
        System.out.println("✅ Validations:");
        System.out.println(validationService.getAllData());

        //affichage
        System.out.println(dossierService.getAllData());
        System.out.println(declarationS.getAllData());
        // 📌 Création d'une assistance documentaire
        // Note: Now using idDocument (an integer) instead of documentsRequis.
        // Here, we use the id of doc2 as the idDocument for testing.
        AssistantDocumentaire assistant1 = new AssistantDocumentaire(1, utilisateur.getId(), doc2.getId(), "Renouvellement", "2025-02-11 10:00:00", "En attente", "Vérification nécessaire", true);
        AssistantDocumentaire assistant2 = new AssistantDocumentaire(2, utilisateur.getId(), doc2.getId(), "Nouveau document", "2025-02-12 15:30:00", "Traitée", "Documents validés", false);

        // ✅ Ajout des assistances documentaires
        assistantService.addEntity(assistant1);
        assistantService.addEntity(assistant2);

        // 📄 Affichage des assistances documentaires
        System.out.println("📄 Assistances Documentaires:");
        System.out.println(assistantService.getAllData());

        //exporter
        dossierService.ExportPDF(dossier3);
        dossierService.ExportExcel("data");
        // 🔄 Mise à jour d'une assistance documentaire
        assistant2.setStatus("Rejetée");
        assistant2.setRemarque("Documents incorrects");
        assistantService.updateEntity(assistant2);

        // 🗑️ Suppression d'une assistance documentaire
        assistantService.deleteEntity(assistant1);

        // 📄 Affichage des assistances après suppression
        System.out.println("📄 Assistances après suppression:");
        System.out.println(assistantService.getAllData());

//        Lampadaire p=new Lampadaire(3, "Rue de Bourguiba", true, 150.3);
//        Quartier q=new Quartier(2,4,150.0);
//        LampadaireService ps=new LampadaireService();
//        QuartierService qs = new QuartierService();
//        qs.updateEntity(q);
//        //ps.addEntity(p);
//        System.out.println(ps.getAllData());
//        System.out.println(qs.getAllData());
        // 📄 Génération du PDF
        PDFGenerator.generatePDF(doc2);

        // 📄 Génération du fichier Excel
        ExcelGenerator.exportToExcel("documents_admin");

    }
}
