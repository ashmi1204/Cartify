package com.cartify.utils;

import java.sql.*;

public class DatabaseConnection {
    private static final DatabaseConnection instance = new DatabaseConnection();
    
    // UPDATE THESE WITH YOUR ACTUAL MYSQL CREDENTIALS
    private static final String URL = "jdbc:mysql://localhost:3306/cartify";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Anooj*12345678"; // CHANGE THIS!
    
    private Connection connection;
    
    private DatabaseConnection() {
        initializeConnection();
    }
    
    public static DatabaseConnection getInstance() {
        return instance;
    }
    
    private void initializeConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");
            
            // Establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ DATABASE CONNECTED SUCCESSFULLY!");
            System.out.println("Connected to database: " + connection.getCatalog());
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ DATABASE CONNECTION FAILED!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. Is MySQL running?");
            System.err.println("2. Does database 'cartify' exist?");
            System.err.println("3. Are username and password correct?");
            System.err.println("4. URL: " + URL);
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Reconnecting to database...");
                initializeConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // Test if connection is working
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}