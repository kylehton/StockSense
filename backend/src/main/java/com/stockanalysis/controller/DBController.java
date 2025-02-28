package com.stockanalysis.controller;
import com.stockanalysis.config.DataBaseConfig;

import jakarta.servlet.http.HttpSession;

import com.stockanalysis.service.DBService;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBController {

    Statement stmt;
    Connection conn;
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
        String google_id = session.getAttribute("USER_ID").toString();
        return dbService.getSymbols(google_id, this.stmt);
    }

    @GetMapping("/check")
    public Boolean checkUser(HttpSession session) {
        String google_id = session.getAttribute("USER_ID").toString();
        System.out.println("User Check Results: "+dbService.checkUserExists(google_id, this.stmt));
        boolean exists = dbService.checkUserExists(google_id, this.stmt);
        if (!exists) {
            try{
            String email = session.getAttribute("EMAIL").toString();
            String addUser = dbService.addUserToDB(google_id, email, this.stmt);
            System.out.println(addUser);
            return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @PostMapping("/adduser")
    public String addUser(HttpSession session) {
        System.out.println("Adding user");
        try{
        String google_id = session.getAttribute("USER_ID").toString();
        String email = session.getAttribute("EMAIL").toString();
        return dbService.addUserToDB(google_id, email, this.stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add user: "+e;
        }
    }

    @PostMapping("/add")
    public String addToDB(HttpSession session, @RequestParam String symbol) {
        try{
        String user_id = session.getAttribute("USER_ID").toString();
        dbService.addSymbol(user_id, this.stmt, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add symbol: "+e;
        }
        return "Successfully added symbol: "+symbol;
    }

    @DeleteMapping("/delete")
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

}


