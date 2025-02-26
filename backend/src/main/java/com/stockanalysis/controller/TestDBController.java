package com.stockanalysis.controller;
import com.stockanalysis.config.DataBaseConfig;

import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestDBController {

    Statement stmt;
    
    public TestDBController(DataBaseConfig dbConfig) {
        this.stmt = dbConfig.dbStatement();
    }

    @RequestMapping("/testdb")
    public void testDB() {
        try {
            // Execute a query and get a ResultSet
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Process the ResultSet
            while (rs.next()) {
                String email = rs.getString("email");  
                System.out.println("Email: " + email);
            }

            // Close the connections
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
