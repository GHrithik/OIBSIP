package com.reservationsystem;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/reservationsystem_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    public static Connection connect() {
        Connection conn = null;
        try {

            Class.forName("org.postgresql.Driver");


            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception ex) {
            System.out.println("❌ Database connection error: " + ex.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        if (connect() != null) {
            System.out.println("✅ Connected to PostgreSQL successfully!");
        } else {
            System.out.println("❌ Failed to connect to PostgreSQL.");
        }
    }
}
