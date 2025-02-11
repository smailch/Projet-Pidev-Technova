package test;

import entities.AssistantDocumentaire;
import entities.DocumentAdministratif;
import entities.Validation;
import services.AssistantDocumentaireService;
import services.DocumentAdministratifService;
import services.ValidationService;
import services.PDFGenerator;
import services.ExcelGenerator;
import tools.MyConnection;

public class MainClass {
    public static void main(String[] args) {
        // 🔹 Initialisation de la connexion
        MyConnection mc = MyConnection.getInstance();

        // 🔹 Service de gestion des documents administratifs
        DocumentAdministratifService docService = new DocumentAdministratifService();

        // 🔹 Service de gestion des validations
        ValidationService validationService = new ValidationService();

        // 🔹 Service de gestion de l'Assistant Documentaire
        AssistantDocumentaireService assistantService = new AssistantDocumentaireService();

        // 📌 Création de documents administratifs
        DocumentAdministratif doc1 = new DocumentAdministratif(1, 1, "Justificatif de domicile", "/projet/docs", "2025-01-03", "Validé", "Aucune remarque");
        DocumentAdministratif doc2 = new DocumentAdministratif(2, 1, "Permis de travail", "/projet/docs", "2025-02-10", "En attente", "Vérification en cours");

        // ✅ Ajout des documents
        docService.addEntity(doc1);
        docService.addEntity(doc2);

        // 📄 Affichage des documents
        System.out.println("📄 Documents Administratifs:");
        System.out.println(docService.getAllData());

        // 🔄 Mise à jour d'un document
        doc2.setStatus("Approuvé");
        docService.updateEntity(doc2);

        // 🗑️ Suppression d'un document
        docService.deleteEntity(doc1);

        // 📄 Affichage après suppression
        System.out.println("📄 Documents après suppression:");
        System.out.println(docService.getAllData());

        // 📌 Création d'une validation
        Validation validation1 = new Validation(1, doc2.getId(), "Validé", "12/06/2018", "conforme");
        Validation validation2 = new Validation(2, doc2.getId(), "Rejeté", "27/07/2002", "non conforme");

        // ✅ Ajout des validations
        validationService.addEntity(validation1);
        validationService.addEntity(validation2);

        // 🔄 Mise à jour d'une validation
        validation2.setStatut("Validé");
        validation2.setDateValidation("2025-02-13"); // Mise à jour de la date
        validationService.updateEntity(validation2);

        // 🗑️ Suppression d'une validation
        validationService.deleteEntity(validation1);

        // 📄 Affichage des validations restantes
        System.out.println("✅ Validations:");
        System.out.println(validationService.getAllData());

        // 📌 Création d'une assistance documentaire
        // Note: Now using idDocument (an integer) instead of documentsRequis.
        // Here, we use the id of doc2 as the idDocument for testing.
        AssistantDocumentaire assistant1 = new AssistantDocumentaire(1, 13, doc2.getId(), "Renouvellement", "2025-02-11 10:00:00", "En attente", "Vérification nécessaire", true);
        AssistantDocumentaire assistant2 = new AssistantDocumentaire(2, 13, doc2.getId(), "Nouveau document", "2025-02-12 15:30:00", "Traitée", "Documents validés", false);

        // ✅ Ajout des assistances documentaires
        assistantService.addEntity(assistant1);
        assistantService.addEntity(assistant2);

        // 📄 Affichage des assistances documentaires
        System.out.println("📄 Assistances Documentaires:");
        System.out.println(assistantService.getAllData());

        // 🔄 Mise à jour d'une assistance documentaire
        assistant2.setStatus("Rejetée");
        assistant2.setRemarque("Documents incorrects");
        assistantService.updateEntity(assistant2);

        // 🗑️ Suppression d'une assistance documentaire
        assistantService.deleteEntity(assistant1);

        // 📄 Affichage des assistances après suppression
        System.out.println("📄 Assistances après suppression:");
        System.out.println(assistantService.getAllData());

        // 📄 Génération du PDF
        PDFGenerator.generatePDF(doc2);

        // 📄 Génération du fichier Excel
        ExcelGenerator.exportToExcel("documents_admin");
    }
}
