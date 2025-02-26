package org.example.demo1.models;

public class Poubelle_Intelligente {
    private int id;
    private String type_dechets;
    private double niveau_remplissage;
    private String localisation;
    private int zoneId; // New field for zone ID

    // Getters and Setters
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

    public double getNiveau_remplissage() {
        return niveau_remplissage;
    }

    public void setNiveau_remplissage(double niveau_remplissage) {
        this.niveau_remplissage = niveau_remplissage;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }
}