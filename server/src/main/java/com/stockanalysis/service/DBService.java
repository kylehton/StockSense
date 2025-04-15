package com.stockanalysis.service;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;


public class DBService {

    public Boolean checkUserExists(String google_id, Statement stmt) {
        try {
            Connection conn = stmt.getConnection();
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE google_id = ?");
            pstmt.setString(1, google_id);
            
            // Execute the query and get a ResultSet
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // true if user exists, else false
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String addUserToDB(String google_id, String email, Statement stmt){
        try {
            Connection conn = stmt.getConnection();
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, google_id) VALUES (?, ?)");
            pstmt.setString(1, email);
            pstmt.setString(2, google_id);
            
            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Added user: "+google_id);
            return "Successfully added user: "+email;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add user: "+e;
        }
    }

    public int getId(String google_id, Statement stmt) {
        try {            
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

    public ArrayList<String> getSymbols(String google_id, Statement stmt) {
        ArrayList<String> symbols = new ArrayList<>();
        try {
            Connection conn = stmt.getConnection();
            // Execute a query and get a ResultSet
            System.out.println("Google ID: "+google_id);
            int user_id = getId(google_id, stmt);
            PreparedStatement pstmt = conn.prepareStatement("SELECT symbol FROM stocks WHERE user_id = ?");
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

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

    public String addSymbol(String google_id, Statement stmt,  String symbol) {
        try {
            Connection conn = stmt.getConnection();
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stocks (user_id, symbol) VALUES (?, ?) ON CONFLICT (user_id, symbol) DO NOTHING");
            int user_id = getId(google_id, stmt);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, symbol);
            
            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Added symbol: "+symbol);
            return "Successfully added symbol: "+symbol;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add symbol: "+e;
        }
    }

    public String deleteSymbol(String google_id, Statement stmt, String symbol) {
        try {
            Connection conn = stmt.getConnection();
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM stocks WHERE user_id = ? AND symbol = ?");
            int user_id = getId(google_id, stmt);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, symbol);
            
            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Deleted symbol: "+symbol);
            return "Successfully deleted symbol: "+symbol;


        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete symbol: "+symbol;

        }
    }

    public String storeNewsKey(String symbol, String key, Statement stmt) {
        try {
            Connection conn = stmt.getConnection();
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement pstmt = conn.prepareStatement("UPDATE stocks SET newskey = ? WHERE symbol = ?");
            pstmt.setString(1, key);

            pstmt.setString(2, symbol);
            
            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Stored news key: "+key);
            return key;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to store news key: "+e;
        }
    }

    public String getNewsKey(String symbol, Statement stmt)
    {
        try 
        {
            Connection conn = stmt.getConnection();
            System.out.println("GETTING NEWS KEY for: "+symbol);
            PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT newskey FROM stocks WHERE symbol = ?");
            pstmt.setString(1, symbol);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                String key = rs.getString("newskey");
                System.out.println("News Key: "+key);
                return key;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Failed to get news key: "+e;
        }
        return "Failed to get news key";
    }
}

