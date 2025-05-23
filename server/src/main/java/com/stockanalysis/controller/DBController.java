package com.stockanalysis.controller;
import com.stockanalysis.config.DataBaseConfig;

import jakarta.servlet.http.HttpSession;

import com.stockanalysis.service.DBService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/db") // Base URL for all endpoints in this controller
public class DBController {

    Statement stmt;
    Connection conn;
    DBService dbService;
    
    @Autowired
    public DBController(DataBaseConfig dbConfig) {
        try {
            this.stmt = dbConfig.dbStatement();
            this.dbService = new DBService();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @GetMapping("/testdb")
    public void testDB() {
        try {
            // Execute a query and get a ResultSet
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            // Process the ResultSet
            while (rs.next()) {
                String email = rs.getString("email");  
                System.out.println("Email: " + email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/getsymbols")
    public ArrayList<String> getSymbolsFromDB(HttpSession session) {
        String google_id = session.getAttribute("USER_ID").toString();
        return dbService.getSymbols(google_id, this.stmt);
    }

    @GetMapping("/check")
    public Boolean checkUser(HttpSession session) {
        try {
            System.out.println("Current session: "+session.getId());
            System.out.println("Searching for user id: "+session.getAttribute("USER_ID"));
            String google_id = session.getAttribute("USER_ID").toString();
            return dbService.checkUserExists(google_id, this.stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }

    @PostMapping("/adduser")
    public String addUser(HttpSession session) 
    {
        try {
            String google_id = session.getAttribute("USER_ID").toString();
            String email = session.getAttribute("email").toString();
            return dbService.addUserToDB(google_id, email, this.stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add user: "+e;
        }
    }

    @PostMapping("/addsymbol")
    public String addToDB(HttpSession session, @RequestParam String symbol) {
        try {
        String user_id = session.getAttribute("USER_ID").toString();
        dbService.addSymbol(user_id, this.stmt, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add symbol: "+e;
        }
        return "Successfully added symbol: "+symbol;
    }

    @DeleteMapping("/deletesymbol")
    public String DeleteFromDB(HttpSession session, @RequestParam String symbol) {
        try{
        String google_id = session.getAttribute("USER_ID").toString();
        dbService.deleteSymbol(google_id, this.stmt, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete symbol: "+e;
        }
        return "Successfully deleted symbol: "+symbol;
    }

    @PostMapping("/setnewskey")
    public String setS3NewsKey(HttpSession session, @RequestParam String symbol, @RequestParam String key) {
        try
        {
            return dbService.storeNewsKey(symbol, key, this.stmt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Failed to store news key: "+e;
        }
    }

    @GetMapping("/getnewskey")
    public String getS3NewsKey(@RequestParam String symbol) {
        try
        {
            return dbService.getNewsKey(symbol, this.stmt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Failed to get news key: "+e;
        }
    }

}


