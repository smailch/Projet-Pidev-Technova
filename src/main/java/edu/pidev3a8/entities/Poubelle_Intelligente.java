package edu.pidev3a8.entities;

public class Poubelle_Intelligente {

    private int id;
    private String type_dechets;
    private Double niveau_remplissage;
    private String 	localisation;

    public Poubelle_Intelligente(int id, String type_dechets, Double niveau_remplissage, String localisation) {
        this.id = id;
        this.type_dechets = type_dechets;
        this.niveau_remplissage = niveau_remplissage;
        this.localisation = localisation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_dechets() {
        return type_dechets;
    }

    public void setType_dechets(String type_dechets) {
        this.type_dechets = type_dechets;
    }

    public Double getNiveau_remplissage() {
        return niveau_remplissage;
    }

    public void setNiveau_remplissage(Double niveau_remplissage) {
        this.niveau_remplissage = niveau_remplissage;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Poubelle_Intelligente() {
    }
    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", type_dechets='" + type_dechets + '\'' +
                ", niveau_remplissage='" + niveau_remplissage + '\'' +
                ", localisation='" + localisation + '\'' +
                '}';
    }
}
