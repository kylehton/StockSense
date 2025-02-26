package com.stockanalysis.service;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {

    private S3Service S3Service;

    public SentimentAnalysisService(S3Service S3Service) {
        this.S3Service = S3Service;
    }
    
    public String analyzeSentiment(String key) {
        String text = S3Service.readObjectContent(key);
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                .setContent(text)
                .setType(Type.PLAIN_TEXT)
                .build();
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            int score = (int) (sentiment.getScore());
            int magnitude = (int) (sentiment.getMagnitude());
            return ("Score: " + score + ", Magnitude: " + magnitude);
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing sentiment: " + e.getMessage(), e);
        }
    }
}

