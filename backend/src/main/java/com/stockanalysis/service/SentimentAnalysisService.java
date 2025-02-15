package com.stockanalysis.service;

import com.stockanalysis.config.AWSConfig;

import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;

import org.springframework.stereotype.Service;


@Service
public class SentimentAnalysisService {
 
    private ComprehendClient comprehendClient;

    // build client for AWS Comprehend through AWSConfig
    public SentimentAnalysisService(AWSConfig awsConfig) {
        this.comprehendClient = awsConfig.buildComprehendClient();
    }

    // score text based on positive, negative, neutral, or mixed sentiment
    public String analyzeSentiment(String text) {

        // Create the sentiment request
        DetectSentimentRequest sentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        // Call AWS Comprehend
        DetectSentimentResponse sentimentResponse = comprehendClient.detectSentiment(sentimentRequest);
        
        // Return the sentiment classification
        return sentimentResponse.sentimentAsString();
    }
}
