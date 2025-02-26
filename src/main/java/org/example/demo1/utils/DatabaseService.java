package org.example.demo1.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {
    private static final String URL = "jdbc:mysql://localhost:3306/pidev";
    private static final String USER = "root"; // Default XAMPP MySQL username
    private static final String PASSWORD = ""; // Default XAMPP MySQL password

    // Establish a database connection
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
        return conn;
    }
}