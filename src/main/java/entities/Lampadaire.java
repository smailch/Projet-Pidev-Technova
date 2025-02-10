package entities;

public class Lampadaire {
    private int id;
    private String localisation;
    private boolean etat; // true pour allumé, false pour éteint
    private double consommation;
    private Quartier quartier; // Relation Many-to-One

    // Constructeurs
    public Lampadaire() {}
    public Lampadaire(int id, Quartier quartier) {
        this.id = id;
        this.quartier = quartier;
    }

    public Lampadaire(int id, String localisation, boolean etat, double consommation, Quartier quartier) {
        this.id = id;
        this.localisation = localisation;
        this.etat = etat;
        this.consommation = consommation;
        this.quartier = quartier;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public double getConsommation() {
        return consommation;
    }

    public void setConsommation(double consommation) {
        this.consommation = consommation;
    }

    public Quartier getQuartier() {
        return quartier;
    }

    public void setQuartier(Quartier quartier) {
        this.quartier = quartier;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "Lampadaire{" +
                "id=" + id +
                ", localisation='" + localisation + '\'' +
                ", etat=" + etat +
                ", consommation=" + consommation +
                ", quartier=" + quartier +
                '}';
    }
}