package com.stockanalysis.controller;
import com.stockanalysis.service.GenerateNewsService;
import com.stockanalysis.service.S3Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class GenerateNewsController {
    
    private final GenerateNewsService generateNewsService;
    private final S3Service s3Service;

    @Autowired
    public GenerateNewsController(GenerateNewsService generateNewsService, S3Service s3Service) {
        this.generateNewsService = generateNewsService;
        this.s3Service = s3Service;
        
    }

    @PostMapping("/generate")
    public String generateNews(@RequestParam String symbol) {
        // generates news, places it into S3 bucket, then returns its respective S3 key
        String[][] news = generateNewsService.generateNews(symbol);
        String s3Key = s3Service.uploadAVNewsData(symbol, news);
        return s3Key;
    }

}
