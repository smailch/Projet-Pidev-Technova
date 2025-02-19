package entities;

public class Quartier {
    private int id;
    private String nom;
    private int nbLamp;
    private double consomTot;

    // Default constructor
    public Quartier() {
    }

    // Constructor with id only
    public Quartier(int id) {
        this.id = id;
    }

    // Parameterized constructor
    public Quartier(int id, String nom, int nbLamp, double consomTot) {
        this.id = id;
        this.nom = nom;
        this.nbLamp = nbLamp;
        this.consomTot = consomTot;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbLamp() {
        return nbLamp;
    }

    public void setNbLamp(int nbLamp) {
        this.nbLamp = nbLamp;
    }

    public double getConsomTot() {
        return consomTot;
    }

    public void setConsomTot(double consomTot) {
        this.consomTot = consomTot;
    }

    @Override
    public String toString() {
        return "Quartier{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", nbLamp=" + nbLamp +
                ", consomTot=" + consomTot +
                '}';
    }

    public void addLampadaire(Lampadaire p) {
    }
}