package com.stockanalysis.controller;
import com.stockanalysis.service.GoogleAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/google")
public class GoogleAuthenticationController {
    
    private GoogleAuthenticationService googleAuthenticationService;

    @Autowired
    public GoogleAuthenticationController(GoogleAuthenticationService googleAuthenticationService)
    {
        this.googleAuthenticationService = googleAuthenticationService;
    }

    @RequestMapping("/auth")
    public String authenticateUser(@RequestParam String idToken)
    {
        googleAuthenticationService.authenticate(idToken);
        return "Authenticated the following user: ";
    }

}
