package com.stockanalysis.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Configuration
public class DataBaseConfig {

    Connection conn;
    
    public Statement dbStatement() {

        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

     
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
