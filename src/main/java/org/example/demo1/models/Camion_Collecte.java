package org.example.demo1.models;

public class Camion_Collecte {

    private int id;
    private Double capacite_max;
    private String statut;
    private int zone_id;

    // Default constructor (no-argument constructor)
    public Camion_Collecte() {
    }

    // Parameterized constructor
    public Camion_Collecte(int id, Double capacite_max, String statut, int zone_id) {
        this.id = id;
        this.capacite_max = capacite_max;
        this.statut = statut;
        this.zone_id = zone_id;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getCapacite_max() {
        return capacite_max;
    }

    public void setCapacite_max(Double capacite_max) {
        this.capacite_max = capacite_max;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    // toString method for easy object representation
    @Override
    public String toString() {
        return "Camion_Collecte{" +
                "id=" + id +
                ", capacite_max=" + capacite_max +
                ", statut='" + statut + '\'' +
                ", zone_id=" + zone_id +
                '}';
    }
}