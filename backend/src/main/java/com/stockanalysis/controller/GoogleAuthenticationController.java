package com.stockanalysis.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.stockanalysis.service.GoogleAuthenticationService;
import java.io.IOException;
import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/auth")
    public ResponseEntity<String> authenticateUser(@RequestParam String id, HttpSession session) {
        try {

            //CsrfToken csrfToken = (CsrfToken) session.getAttribute(CsrfToken.class.getName());
            
            System.out.println(id);
            GoogleIdToken.Payload payload = googleAuthenticationService.authenticate(id);
            String userId = payload.getSubject();
            
            System.out.println("Current Session ID: " + session.getId());
            System.out.println("Storing userID: " + session.getAttribute("USER_ID"));
            System.out.println("Storing email: " + session.getAttribute("EMAIL"));
            session.setAttribute("USER_ID", userId);
            session.setAttribute("EMAIL", payload.get("email"));

            //HttpHeaders headers = new HttpHeaders();
            //System.out.println("CSRF Token: " + csrfToken.getToken());
            //headers.set("X-XSRF-TOKEN", csrfToken.getToken());

            return ResponseEntity.ok()
            //.headers(headers)
            .body("Authenticated user: " + payload.get("email"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Authentication error: " + e.getMessage());
        }
    }

}