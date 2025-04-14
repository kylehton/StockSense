package com.stockanalysis.config;

import org.springframework.context.annotation.Configuration;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataBaseConfig {

    Connection conn;
    
    public Statement dbStatement() {

        @Value("${DB_URL}")
        String url;

        @Value("${DB_USER}")
        String user;

        @Value("${DB_PASSWORD}")
        String password;

     
            // Connect to the PostgreSQL database
        try {
            this.conn = DriverManager.getConnection(url, user, password);

            // Create a Statement object
            Statement stmt = conn.createStatement();
            return stmt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void closeConnection(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                (this.conn).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
