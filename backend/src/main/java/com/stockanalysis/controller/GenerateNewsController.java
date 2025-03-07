package com.stockanalysis.controller;
import com.stockanalysis.service.GenerateNewsService;
import com.stockanalysis.service.S3Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenerateNewsController {
    
    private final GenerateNewsService generateNewsService;
    private final S3Service s3Service;

    @Autowired
    public GenerateNewsController(GenerateNewsService generateNewsService, S3Service s3Service) {
        this.generateNewsService = generateNewsService;
        this.s3Service = s3Service;
        
    }

    @PostMapping("/generatenews")
    public String[][] generateNews(@RequestParam String symbol) {
        // generates news, places it into S3 bucket, then returns its respective S3 key
        String[][] news = generateNewsService.generateNews(symbol);
        System.out.println("News: "+ news[0][0] + news[1][0] + news[2][0] + news[3][0]);
        String s3Key = s3Service.uploadAVNewsData(symbol, news);
        System.out.println("S3 Key: " + s3Key);
        return news;
    }

    @GetMapping("/getnews")
    public String[][] getNews(@RequestParam String key) {
        // retrieves news from S3 bucket using the S3 key
        return s3Service.readNewsObjectContent(key);
    }

}
