package com.stockanalysis.controller;
import com.stockanalysis.service.GenerateNewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenerateNewsController {
    
    private final GenerateNewsService generateNewsService;

    @Autowired
    public GenerateNewsController(GenerateNewsService generateNewsService) {
        this.generateNewsService = generateNewsService;
        
    }

    @GetMapping("/getnews")
    public String[][] generateNews(@RequestParam String symbol) {
        return generateNewsService.generateNews(symbol);
    }

}
