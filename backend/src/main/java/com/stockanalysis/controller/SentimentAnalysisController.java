package com.stockanalysis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockanalysis.models.SentimentResult;
import com.stockanalysis.service.SentimentAnalysisService;

@RestController
public class SentimentAnalysisController {

    private SentimentAnalysisService sentimentAnalysisService;

    public SentimentAnalysisController(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }
    
    @GetMapping("/analyze")
    public ResponseEntity<SentimentResult> analyzeSentiment(@RequestParam String key) {
        SentimentResult result = sentimentAnalysisService.analyzeSentiment(key);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/analyze-batch")
    public ResponseEntity<?> analyzeBatchSentiment(@RequestBody String[] s3Keys) {
        // Placeholder for batch processing
        // Implementation would depend on how you want to return multiple results
        return ResponseEntity.ok().body("Batch processing initiated for " + s3Keys.length + " files");
    }
    
}
