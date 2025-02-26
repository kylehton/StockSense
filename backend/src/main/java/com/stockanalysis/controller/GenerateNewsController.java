package com.stockanalysis.controller;
import com.stockanalysis.service.GenerateNewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class GenerateNewsController {
    
    private final GenerateNewsService generateNewsService;

    @Autowired
    public GenerateNewsController(GenerateNewsService generateNewsService, HttpSession httpSession) {
        this.generateNewsService = generateNewsService;
        
    }

    @GetMapping("/getnews")
    public String generateNews(@RequestParam String symbol, HttpSession session) {
        System.out.println("Stock symbol: " + symbol);
        generateNewsService.generateNews(symbol, session.getAttribute("USER_ID").toString());
        return "News generated successfully for " + symbol;
    }

}
