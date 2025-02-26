package com.stockanalysis.controller;
import com.stockanalysis.service.GenerateNewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Date;

@RestController
public class GenerateNewsController {
    
    private final GenerateNewsService generateNewsService;

    @Autowired
    public GenerateNewsController(GenerateNewsService generateNewsService) {
        this.generateNewsService = generateNewsService;
        
    }

    @GetMapping("/getnews")
    public String generateNews(@RequestParam String symbol, HttpSession session) {
        System.out.println("Stock symbol: " + symbol);
        System.out.println("all session attributes: " + session.getAttributeNames());
        System.out.println("Current Session ID: " + session.getId());
        System.out.println("Session Creation Time: " + new Date(session.getCreationTime()));
        System.out.println("Session Last Accessed Time: " + new Date(session.getLastAccessedTime()));
        System.out.println("Is Session New: " + session.isNew());
        generateNewsService.generateNews(symbol, session.getAttribute("USER_ID").toString());
        return "News generated successfully for " + symbol;
    }

}
