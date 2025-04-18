package com.stockanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DataBaseConfig {

    private Connection conn;

    @Value("${DATABASE_URL}")
    private String url;

    @Value("${DB_USER}")
    private String user;

    @Value("${DB_PASSWORD}")
    private String password;
    
    public Statement dbStatement() throws SQLException {
        // Connect to the PostgreSQL database
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, password);
        }
        
        // Create a Statement object
        Statement stmt = conn.createStatement();
        return stmt;
    }

    public void closeConnection(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}