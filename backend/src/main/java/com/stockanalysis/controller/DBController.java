package com.stockanalysis.controller;
import com.stockanalysis.config.DataBaseConfig;

import jakarta.servlet.http.HttpSession;

import com.stockanalysis.service.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    private static final Logger logger = LoggerFactory.getLogger(DBController.class);

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
        try {
            System.out.println("Fetching symbols from DB . . .");
            Object userId = session.getAttribute("USER_ID");
            if (userId == null) {
                System.out.println("No user session found for getSymbols");
                return new ArrayList<>();
            }
            String google_id = userId.toString();
            return dbService.getSymbols(google_id, this.stmt);
        } catch (Exception e) {
            System.out.println("Error fetching symbols: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkUser(HttpSession session) {
        logger.debug("Check endpoint called. Session ID: {}", 
            session != null ? session.getId() : "null");

        try {
            // Check if there's an active session
            if (session == null) {
                logger.debug("No session found");
                return ResponseEntity.ok(false);
            }

            // Get user ID from session
            Object userId = session.getAttribute("USER_ID");
            logger.debug("USER_ID from session: {}", userId);

            if (userId == null) {
                logger.debug("No USER_ID in session");
                return ResponseEntity.ok(false);
            }

            // Check if user exists in database
            boolean exists = dbService.checkUserExists(userId.toString(), stmt);
            logger.debug("User exists in DB: {}", exists);

            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            logger.error("Error checking user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error checking user: " + e.getMessage());
        }
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


