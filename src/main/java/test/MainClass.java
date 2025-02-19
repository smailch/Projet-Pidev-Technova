package test;
import entities.Lampadaire;
import entities.Quartier;
import services.LampadaireService;
import services.QuartierService;
import tools.MyConnection;

import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        // Établir la connexion à la base de données
        MyConnection mc = MyConnection.getInstance();

        // Créer un quartier
        Quartier q = new Quartier(2);
        QuartierService qs = new QuartierService();

        // Ajouter le quartier à la base de données
        //qs.deleteEntity(q);
        System.out.println(qs.getAllData());



        // Créer un lampadaire associé au quartier
        Lampadaire p = new Lampadaire(17, "Rue de bourguiba, côté nord", true, 150.5, q);
        LampadaireService ps = new LampadaireService();

        // Ajouter le lampadaire à la base de données
       ps.updateEntity(p);
        System.out.println(ps.getAllData());


        // Ajouter le lampadaire au quartier (relation bidirectionnelle)
        q.addLampadaire(p);

    }
}