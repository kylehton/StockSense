package com.stockanalysis.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.stockanalysis.service.GoogleAuthenticationService;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/google")
public class GoogleAuthenticationController {
    
    private GoogleAuthenticationService googleAuthenticationService;

    @Autowired
    public GoogleAuthenticationController(GoogleAuthenticationService googleAuthenticationService)
    {
        this.googleAuthenticationService = googleAuthenticationService;
    }

    @GetMapping("/auth")
    public ResponseEntity<String> authenticateUser(@RequestParam String idToken, HttpSession session)
    {
        System.out.println("Authentication process beginning...");
        try {
            GoogleIdToken.Payload payload = googleAuthenticationService.authenticate(idToken);
            String userId = payload.getSubject();
            session.setAttribute("USER_ID", userId);
            System.out.println("User ID: " + session.getAttribute("USER_ID"));
            return ResponseEntity.ok("Authenticated the following user: " + payload.get("email"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Authentication failed due to an error: " + e.getMessage());
        }
    }

}