package entities;

import java.util.ArrayList;
import java.util.List;


public class Quartier {
    private int id;
    private int nbLamp;
    private double consom_tot;
    private List<Lampadaire> lampadaires;

    // Constructeurs
    public Quartier() {
        this.lampadaires = new ArrayList<>();
    }

    public Quartier(int id, int nbLamp, double consom_tot) {
        this.id = id;
        this.nbLamp = nbLamp;
        this.consom_tot = consom_tot;
        this.lampadaires = new ArrayList<>();

    }

    public Quartier(int id) {
        this.id = id;
        this.lampadaires = new ArrayList<>();

    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbLamp() {
        return nbLamp;
    }

    public void setNbLamp(int nbLamp) {
        this.nbLamp = nbLamp;
    }

    public double getConsom_tot() {
        return consom_tot;
    }

    public void setConsom_tot(double consom_tot) {
        this.consom_tot = consom_tot;
    }

    public List<Lampadaire> getLampadaires() {
        return lampadaires;
    }

    public void setLampadaires(List<Lampadaire> lampadaires) {
        this.lampadaires = lampadaires;
    }

    // Méthode pour ajouter un lampadaire au quartier
    public void addLampadaire(Lampadaire lampadaire) {
        this.lampadaires.add(lampadaire);
        lampadaire.setQuartier(this); // Définir le quartier du lampadaire
    }

    // Méthode toString pour afficher les informations du quartier
    @Override
    public String toString() {
        return "Quartier{" +
                "id=" + id +
                ", nombreLampadaires=" + nbLamp +
                ", consommationTotale=" + consom_tot +
                '}';
    }
}