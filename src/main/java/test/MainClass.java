package test;

import entities.Lampadaire;
import entities.Quartier;
import services.LampadaireService;
import services.QuartierService;
import tools.MyConnection;

public class MainClass {
    public static void main(String[] args){
        MyConnection mc=MyConnection.getInstance();
        Lampadaire p=new Lampadaire(3, "Rue de Bourguiba", true, 150.3);
        Quartier q=new Quartier(2,4,150.0);
        LampadaireService ps=new LampadaireService();
        QuartierService qs = new QuartierService();
        qs.updateEntity(q);
        //ps.addEntity(p);
        System.out.println(ps.getAllData());
        System.out.println(qs.getAllData());
    }
}
