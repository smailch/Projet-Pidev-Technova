package org.example.demo1.models;

public class Zone {
    private int id;
    private String location;

    // Constructor with ID and location
    public Zone(int id, String location) {
        this.id = id;
        this.location = location;
    }

    // Constructor with only location (ID will be auto-generated)
    public Zone(String location) {
        this.location = location;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
