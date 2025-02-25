package com.example.demo.models;

import java.time.LocalDate;

public class Lampadaire {
    private int id;
    private String localisation;
    private boolean etat;
    private double consommation;
    private int id_quartier;
    private LocalDate date_installation; // New field

    // Default constructor
    public Lampadaire() {
    }

    // Parameterized constructor
    public Lampadaire(int id, String localisation, boolean etat, double consommation, int id_quartier, LocalDate date_installation) {
        this.id = id;
        this.localisation = localisation;
        this.etat = etat;
        this.consommation = consommation;
        this.id_quartier = id_quartier;
        this.date_installation = date_installation;
    }

    // Getters and Setters
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

    public int getId_quartier() {
        return id_quartier;
    }

    public void setId_quartier(int id_quartier) {
        this.id_quartier = id_quartier;
    }

    public LocalDate getDate_installation() {
        return date_installation;
    }

    public void setDate_installation(LocalDate date_installation) {
        this.date_installation = date_installation;
    }

    @Override
    public String toString() {
        return "Lampadaire{" +
                "id=" + id +
                ", localisation='" + localisation + '\'' +
                ", etat=" + etat +
                ", consommation=" + consommation +
                ", id_quartier=" + id_quartier +
                ", date_installation=" + date_installation +
                '}';
    }
}