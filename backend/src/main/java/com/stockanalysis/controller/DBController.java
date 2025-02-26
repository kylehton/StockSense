package com.stockanalysis.controller;
import com.stockanalysis.config.DataBaseConfig;

import jakarta.servlet.http.HttpSession;

import com.stockanalysis.service.DBService;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBController {

    Statement stmt;
    DBService dbService;
    
    @Autowired
    public DBController(DataBaseConfig dbConfig) {
        this.stmt = dbConfig.dbStatement();
        this.dbService = new DBService();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getsymbols")
    public ArrayList<String> getSymbolsFromDB(HttpSession session) {
        return dbService.getSymbols(session, this.stmt);
    }

    @RequestMapping("/add")
    public String addToDB(HttpSession session, @RequestParam String symbol) {
        try{
        dbService.addSymbol(session, this.stmt, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add symbol: "+e;
        }
        return "Successfully added symbol: "+symbol;
    }

    @RequestMapping("/delete")
    public String DeleteFromDB(HttpSession session, @RequestParam String symbol) {
        try{
        dbService.deleteSymbol(session, this.stmt, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add symbol: "+e;
        }
        return "Successfully added symbol: "+symbol;
    }

}


