package com.stockanalysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalysis.service.SentimentAnalysisService;

@RestController
public class SentimentAnalysisController {

    private SentimentAnalysisService sentimentAnalysisService;

    public SentimentAnalysisController(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }
    
    @GetMapping("/analyze-sentiment")
    public String analyze(@RequestParam String key) {
        String results = sentimentAnalysisService.analyzeSentiment(key);
        return "Sentiment analysis result for key: " + key + "\tResults: "+ results;
    }
}
