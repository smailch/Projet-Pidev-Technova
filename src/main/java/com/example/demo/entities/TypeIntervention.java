package com.example.demo.entities;

public enum TypeIntervention {
    VOIRIE, ECLAIRAGE, PROPRETE, AUTRE;

    // Méthode pour obtenir un TypeIntervention à partir d'une chaîne de caractères
    public static TypeIntervention fromString(String type) {
        try {
            return TypeIntervention.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type d'intervention inconnu : " + type);
        }
    }
}
