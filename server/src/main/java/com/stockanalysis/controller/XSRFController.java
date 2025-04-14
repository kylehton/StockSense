package com.stockanalysis.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class XSRFController {

    @GetMapping("/xsrf")
    public ResponseEntity<Map<String, String>> getXsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "XSRF token not found"));
        }

        CsrfToken token = csrfToken.getToken(); // ✅ Force generation and session storage
        System.out.println("Backend-generated XSRF Token: " + token);

        return ResponseEntity.ok()
                .header("X-XSRF-TOKEN", csrfToken.getToken())
                .body(Map.of("token", csrfToken.getToken()));
    }
}

