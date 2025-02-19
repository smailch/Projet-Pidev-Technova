package edu.pidev3a8.entities;

public class Camion_Collecte {

    private int id;
    private Double capacite_max;
    private String zone_assigne;
    private String statut;

    public Camion_Collecte(int id, Double capacite_max, String zone_assigne, String statut) {
        this.id = id;
        this.capacite_max = capacite_max;
        this.zone_assigne = zone_assigne;
        this.statut = statut;
    }

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

    public String getZone_assigne() {
        return zone_assigne;
    }

    public void setZone_assigne(String zone_assigne) {
        this.zone_assigne = zone_assigne;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Camion_Collecte() {
    }

    @Override
    public String toString() {
        return "Camion_Collecte{" +
                "id=" + id +
                ", capacite_max=" + capacite_max +
                ", zone_assigne='" + zone_assigne + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
