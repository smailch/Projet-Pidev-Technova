package com.example.demo.services;

import com.example.demo.models.Lampadaire;
import com.example.demo.utils.DatabaseService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LampadaireService {

    private DatabaseService databaseService;

    public LampadaireService() {
        this.databaseService = new DatabaseService();
    }

    // Create a new Lampadaire
    public void createLampadaire(Lampadaire lampadaire) throws SQLException {
        String query = "INSERT INTO lampadaire (localisation, etat, consommation, id_quartier, date_installation) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, lampadaire.getLocalisation());
            preparedStatement.setBoolean(2, lampadaire.isEtat());
            preparedStatement.setDouble(3, lampadaire.getConsommation());
            preparedStatement.setInt(4, lampadaire.getId_quartier());
            preparedStatement.setDate(5, Date.valueOf(lampadaire.getDate_installation())); // Convert LocalDate to SQL Date
            preparedStatement.executeUpdate();
        }
    }

    // Get all Lampadaires
    public List<Lampadaire> getAllLampadaires() throws SQLException {
        List<Lampadaire> lampadaires = new ArrayList<>();
        String query = "SELECT * FROM lampadaire";
        try (Connection connection = databaseService.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Lampadaire lampadaire = new Lampadaire();
                lampadaire.setId(resultSet.getInt("id"));
                lampadaire.setLocalisation(resultSet.getString("localisation"));
                lampadaire.setEtat(resultSet.getBoolean("etat"));
                lampadaire.setConsommation(resultSet.getDouble("consommation"));
                lampadaire.setId_quartier(resultSet.getInt("id_quartier")); // Corrected column name
                lampadaire.setDate_installation(resultSet.getDate("date_installation").toLocalDate()); // Corrected column name
                lampadaires.add(lampadaire);
            }
        }
        return lampadaires;
    }

    // Get a Lampadaire by ID
    public Lampadaire getLampadaireById(int id) throws SQLException {
        Lampadaire lampadaire = null;
        String query = "SELECT * FROM lampadaire WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    lampadaire = new Lampadaire();
                    lampadaire.setId(resultSet.getInt("id"));
                    lampadaire.setLocalisation(resultSet.getString("localisation"));
                    lampadaire.setEtat(resultSet.getBoolean("etat"));
                    lampadaire.setConsommation(resultSet.getDouble("consommation"));
                    lampadaire.setId_quartier(resultSet.getInt("id_quartier"));
                    lampadaire.setDate_installation(resultSet.getDate("date_installation").toLocalDate()); // Convert SQL Date to LocalDate
                }
            }
        }
        return lampadaire;
    }

    // Update a Lampadaire
    public void updateLampadaire(Lampadaire lampadaire) throws SQLException {
        String query = "UPDATE lampadaire SET localisation = ?, etat = ?, consommation = ?, id_quartier = ?, date_installation = ? WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, lampadaire.getLocalisation());
            preparedStatement.setBoolean(2, lampadaire.isEtat());
            preparedStatement.setDouble(3, lampadaire.getConsommation());
            preparedStatement.setInt(4, lampadaire.getId_quartier());
            preparedStatement.setDate(5, Date.valueOf(lampadaire.getDate_installation())); // Convert LocalDate to SQL Date
            preparedStatement.setInt(6, lampadaire.getId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete a Lampadaire by ID
    public void deleteLampadaire(int id) throws SQLException {
        String query = "DELETE FROM lampadaire WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    // Update the state (etat) of a Lampadaire by ID
    public void updateLampadaireEtat(int id, boolean newEtat) throws SQLException {
        String query = "UPDATE lampadaire SET etat = ? WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, newEtat);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
}