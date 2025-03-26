package com.stockanalysis.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
public class CSRFController {

    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        System.out.println("Getting csrf token");
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());


        System.out.println("CSRF token: " + csrfToken.getToken());

        return csrfToken; // Return only the token value
    }
}

