package edu.pidev3a8.tests;

import edu.pidev3a8.entities.Poubelle_Intelligente;
import edu.pidev3a8.entities.Camion_Collecte;
import edu.pidev3a8.services.CamionService;
import edu.pidev3a8.tools.MyConnection;
import edu.pidev3a8.services.PoubelleService;
import java.sql.SQLException;
import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        MyConnection mc = MyConnection.getInstance();
        // 🔹 Initialisation des services
        PoubelleService poubelleService = new PoubelleService();
        CamionService camionService = new CamionService();

        // 🗑️ Création de poubelles
        Poubelle_Intelligente p1 = new Poubelle_Intelligente(1, "Plastique", 45.7, "Ariana");
        Poubelle_Intelligente p2 = new Poubelle_Intelligente(2, "Verre", 30.5, "Tunis Centre");
        Poubelle_Intelligente p3 = new Poubelle_Intelligente(3, "Organique", 75.2, "La Marsa");

        // 🚛 Création de camions
        Camion_Collecte c1 = new Camion_Collecte(1, 30.00, "123-TN-456", "Centre-ville");
        Camion_Collecte c2 = new Camion_Collecte(2, 50.00, "789-TN-123", "Banlieue Sud");

        // ✅ Ajout des poubelles et camions dans la base
        poubelleService.addEntity(p1);
        poubelleService.addEntity(p2);
        poubelleService.addEntity(p3);

        camionService.addEntity(c1);
        camionService.addEntity(c2);

        // 📄 Affichage des données initiales
        System.out.println("🗑️ Liste des poubelles :");
        System.out.println(poubelleService.getAllData());

        System.out.println("🚛 Liste des camions :");
        System.out.println(camionService.getAllData());

        // 🔄 Mise à jour des niveaux de remplissage
        p1.setNiveau_remplissage(60.2); // Augmente le niveau de remplissage
        poubelleService.updateEntity(p1);
        poubelleService.updateEntity(p2);
        poubelleService.updateEntity(p3);

        c1.setCapacite_max(100.00);
        camionService.updateEntity(c1);
        camionService.updateEntity(c2);

        // 🗑️ Suppression d'une poubelle et d'un camion
        poubelleService.deleteEntity(p3);
        camionService.deleteEntity(c2);

        // 📄 Affichage après suppression
        System.out.println("🗑️ Liste des poubelles après suppression :");
        System.out.println(poubelleService.getAllData());

        System.out.println("🚛 Liste des camions après suppression :");
        System.out.println(camionService.getAllData());

        System.out.println("✅ Opérations terminées avec succès !");
    }
}
