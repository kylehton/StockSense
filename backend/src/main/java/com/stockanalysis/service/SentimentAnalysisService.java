package com.stockanalysis.service;

import io.github.cdimascio.dotenv.Dotenv;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SentimentAnalysisService {

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    public String analyzeSentiment(String text) {

        Dotenv dotenv = Dotenv.load();

        this.accessKey = dotenv.get("AWS_ACCESS_KEY");
        this.secretKey = dotenv.get("AWS_SECRET_KEY");
        this.region = "us-west-2";
        this.bucketName = dotenv.get("AWS_BUCKET_NAME");

        // Set up AWS credentials
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        
        // Create an Amazon Comprehend client
        ComprehendClient comprehendClient = ComprehendClient.builder()
                .region(Region.of(region))
                .credentialsProvider(() -> awsCreds)
                .build();
        
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
