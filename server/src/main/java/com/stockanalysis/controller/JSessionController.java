package com.stockanalysis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.servlet.http.HttpSession;

@RestController
public class JSessionController {
    
    @GetMapping("/getsession")
    public ResponseEntity<String> getSession(HttpSession session)
    {
        return ResponseEntity.ok().body((session.getId() + " - " + session.getAttribute("USER_ID")));
    }
}
