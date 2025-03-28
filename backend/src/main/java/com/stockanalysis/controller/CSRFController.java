package com.stockanalysis.controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping
public class CSRFController {

    @GetMapping("/xsrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request) {
        System.out.println("Getting CSRF token");
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "XSRF token not found"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-XSRF-TOKEN", csrfToken.getToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of("token", csrfToken.getToken()));
    }
}
