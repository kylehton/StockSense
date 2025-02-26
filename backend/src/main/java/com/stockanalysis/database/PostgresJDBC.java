package com.stockanalysis.database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class PostgresJDBC {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            // Connect to the PostgreSQL database
            Connection conn = DriverManager.getConnection(url, user, password);

            // Create a Statement object
            Statement stmt = conn.createStatement();

            // Execute a query and get a ResultSet
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Process the ResultSet
            while (rs.next()) {
                String name = rs.getString("name");  
                System.out.println("Name: " + name);
            }

            // Close the connections
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

