package com.example.demo.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServiceIntervention {
    private final StringProperty nomService;
    private final StringProperty typeIntervention;
    private final StringProperty zoneIntervention;
    private int id; // ID ne nécessite pas de property

    public ServiceIntervention(int id, String nomService, String typeIntervention, String zoneIntervention) {
        this.id = id;
        this.nomService = new SimpleStringProperty(nomService);
        this.typeIntervention = new SimpleStringProperty(typeIntervention);
        this.zoneIntervention = new SimpleStringProperty(zoneIntervention);
    }

    public String getNomService() {
        return nomService.get();
    }

    public void setNomService(String value) {
        nomService.set(value);
    }

    public StringProperty nomServiceProperty() {
        return nomService;
    }

    public String getTypeIntervention() {
        return typeIntervention.get();
    }

    public void setTypeIntervention(String value) {
        typeIntervention.set(value);
    }

    public StringProperty typeInterventionProperty() {
        return typeIntervention;
    }

    public String getZoneIntervention() {
        return zoneIntervention.get();
    }

    public void setZoneIntervention(String value) {
        zoneIntervention.set(value);
    }

    public StringProperty zoneInterventionProperty() {
        return zoneIntervention;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeInterventionString() {
        return typeIntervention.get();
    }
}
