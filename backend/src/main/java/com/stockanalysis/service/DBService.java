package com.stockanalysis.service;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import jakarta.servlet.http.HttpSession;

public class DBService {

    public int getId(String google_id, Statement stmt) {
        try {
            // Get connection from the statement
            Connection conn = stmt.getConnection();
            
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM users WHERE google_id = ?");
            pstmt.setString(1, google_id);
            
            // Execute the query and get a ResultSet
            ResultSet rs = pstmt.executeQuery();

            // Process the ResultSet
            while (rs.next()) {
                int id = rs.getInt("id");  
                System.out.println("ID:" + id);
                return id;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList<String> getSymbols(HttpSession session, Statement stmt) {
        ArrayList<String> symbols = new ArrayList<>();
        try {
            // Execute a query and get a ResultSet
            String google_id = (String) session.getAttribute("USER_ID");
            System.out.println("Google ID: "+google_id);
            int user_id = getId(google_id, stmt);
            ResultSet rs = stmt.executeQuery("SELECT symbol FROM stocks WHERE user_id = " + user_id);

            // Process the ResultSet
            while (rs.next()) {
                String symbol = rs.getString("symbol");
                symbols.add(symbol);
            }
            System.out.println("Symbols: "+symbols);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbols;
    }
}

