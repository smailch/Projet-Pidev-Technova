package entities;

public class Quartier {
    private int id;
    private int nbLamp;
    private double consom_tot;

    // Constructeurs
    public Quartier() {}

    public Quartier(int id, int nbLamp, double consom_tot) {
        this.id = id;
        this.nbLamp = nbLamp;
        this.consom_tot = consom_tot;
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