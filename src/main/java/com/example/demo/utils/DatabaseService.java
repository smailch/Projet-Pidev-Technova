package com.example.demo.utils;

import java.sql.*;

public class DatabaseService {

    private static final String URL = "jdbc:mysql://localhost:3306/pidev"; // Database URL
    private static final String USER = "root"; // Database user
    private static final String PASSWORD = ""; // Database password

    // Establish a connection to the database
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}