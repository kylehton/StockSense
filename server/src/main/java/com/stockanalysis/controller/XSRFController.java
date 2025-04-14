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

    private static final Logger logger = LoggerFactory.getLogger(XSRFController.class);

    @GetMapping("/xsrf")
    public ResponseEntity<Map<String, String>> getXsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken == null) {
            logger.warn("❌ CSRF token not found in request attributes.");
            return ResponseEntity.badRequest().body(Map.of("error", "XSRF token not found"));
        }

        logger.info("✅ CSRF token fetched: token={}, session={}", csrfToken.getToken(), request.getSession().getId());

        return ResponseEntity.ok(Map.of("token", csrfToken.getToken()));
    }
}
