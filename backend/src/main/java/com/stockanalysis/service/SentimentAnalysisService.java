package com.stockanalysis.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockanalysis.config.HuggingFaceConfig;
import com.stockanalysis.models.SentimentResult;

@Service
public class SentimentAnalysisService {

    private final RestTemplate restTemplate;
    private final HuggingFaceConfig huggingFaceConfig;
    private final ObjectMapper objectMapper;
    private S3Service S3Service;


    @Autowired
    public SentimentAnalysisService(S3Service S3Service,RestTemplate restTemplate, HuggingFaceConfig huggingFaceConfig, ObjectMapper objectMapper) {
        this.S3Service = S3Service;
        this.restTemplate = restTemplate;
        this.huggingFaceConfig = huggingFaceConfig;
        this.objectMapper = objectMapper;
    }
    
    public SentimentResult analyzeSentiment(String key) {
        try {
            // Get the text content from S3
            String text = S3Service.readObjectContent(key);
            
            // Call HuggingFace API
            return analyze(text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze sentiment", e);
        }
    }

    private SentimentResult analyze(String text) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + huggingFaceConfig.getHFKey());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // The FinBERT model endpoint
        String modelEndpoint = "https://api-inference.huggingface.co/models/ProsusAI/finbert";
        
        String response = restTemplate.postForObject(modelEndpoint, entity, String.class);
        
        // Parse the response
        JsonNode rootNode = objectMapper.readTree(response);
        
        // Default values
        String dominantLabel = "neutral";
        double highestScore = 0.0;
        
        // Parse sentiment scores from FinBERT response
        if (rootNode.isArray() && rootNode.size() > 0) {
            JsonNode firstResult = rootNode.get(0);
            
            for (JsonNode scoreNode : firstResult) {
                String label = scoreNode.get("label").asText();
                double score = scoreNode.get("score").asDouble();
                
                // Find the dominant sentiment (highest score)
                if (score > highestScore) {
                    highestScore = score;
                    dominantLabel = label;
                }
            }
        }
        
        // Return using your SentimentResult model
        SentimentResult result = new SentimentResult(dominantLabel, highestScore);
        System.out.println(result.toString());
        return result;
    }
}


