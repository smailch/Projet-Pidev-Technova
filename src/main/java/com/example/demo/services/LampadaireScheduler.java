package com.example.demo.services;

import com.example.demo.utils.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class LampadaireScheduler {

    private DatabaseService databaseService;

    public LampadaireScheduler(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void startScheduler() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    updateAllLampadairesEtat();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000); // Check every minute (60000 milliseconds)
    }

    private void updateAllLampadairesEtat() throws SQLException {
        LocalTime now = LocalTime.now();
        boolean newEtat = !now.isAfter(LocalTime.of(6, 0)) || now.isAfter(LocalTime.of(18, 0));

        String query = "UPDATE lampadaire SET etat = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, newEtat);
            preparedStatement.executeUpdate();
        }
    }
}