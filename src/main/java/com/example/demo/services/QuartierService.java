package com.example.demo.services;

import com.example.demo.models.Quartier;
import com.example.demo.utils.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuartierService {

    private DatabaseService databaseService;

    public QuartierService() {
        this.databaseService = new DatabaseService();
    }

    // Create a new Quartier
    public void createQuartier(Quartier quartier) throws SQLException {
        String query = "INSERT INTO quartier (nom, nbLamp, consomTot) VALUES (?, ?, ?)";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, quartier.getNom());
            preparedStatement.setInt(2, quartier.getNbLamp());
            preparedStatement.setDouble(3, quartier.getConsomTot());
            preparedStatement.executeUpdate();
        }
    }

    // Get all Quartiers
    public List<Quartier> getAllQuartiers() throws SQLException {
        List<Quartier> quartiers = new ArrayList<>();
        String query = "SELECT * FROM quartier";
        try (Connection connection = databaseService.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Quartier quartier = new Quartier();
                quartier.setId(resultSet.getInt("id"));
                quartier.setNom(resultSet.getString("nom"));
                quartier.setNbLamp(resultSet.getInt("nbLamp"));
                quartier.setConsomTot(resultSet.getDouble("consomTot"));
                quartiers.add(quartier);
            }
        }
        return quartiers;
    }

    // Get a Quartier by ID
    public Quartier getQuartierById(int id) throws SQLException {
        Quartier quartier = null;
        String query = "SELECT * FROM quartier WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    quartier = new Quartier();
                    quartier.setId(resultSet.getInt("id"));
                    quartier.setNom(resultSet.getString("nom"));
                    quartier.setNbLamp(resultSet.getInt("nbLamp"));
                    quartier.setConsomTot(resultSet.getDouble("consomTot"));
                }
            }
        }
        return quartier;
    }

    // Update a Quartier
    public void updateQuartier(Quartier quartier) throws SQLException {
        String query = "UPDATE quartier SET nom = ?, nbLamp = ?, consomTot = ? WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, quartier.getNom());
            preparedStatement.setInt(2, quartier.getNbLamp());
            preparedStatement.setDouble(3, quartier.getConsomTot());
            preparedStatement.setInt(4, quartier.getId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete a Quartier by ID
    public void deleteQuartier(int id) throws SQLException {
        String query = "DELETE FROM quartier WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

}