package com.stockanalysis.service;

import com.stockanalysis.config.AWSConfig;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private final S3Client s3Client;
    private String bucketName;
    private final ObjectMapper objectMapper;

    @Autowired
    public S3Service(AWSConfig awsConfig) {
        // Load environment variables using dotenv if not already set
        this.s3Client = awsConfig.buildS3Client();
        this.bucketName = awsConfig.getBucketName();
        this.objectMapper = new ObjectMapper();
    }

    public String uploadAVNewsData(String stockSymbol, String[][] newsArray) {
        try {
            // Generate a unique key for the S3 object using UUID
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String s3Key = "stock_news/" + stockSymbol + "/" + uniqueId + ".json";
            
            // Convert the 2D array to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(newsArray);
            
            // Upload JSON to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType("application/json")
                    .build();
    
            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonPayload, StandardCharsets.UTF_8));
    
            System.out.println("Successfully uploaded news data to S3: " + bucketName + "/" + s3Key);
            return uniqueId;
        } catch (Exception e) {
            System.err.println("Error uploading news data to S3: " + e.getMessage());
            throw new RuntimeException("Failed to upload news data to S3", e);
        }
    }
    

    public String[][] readNewsObjectContent(String objectKey) {
        System.out.println("FULL: "+bucketName+"/"+objectKey);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    
        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
            // Read the content as a string
            String jsonContent = new String(response.readAllBytes());
            
            // Parse the JSON string back to a 2D array
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonContent, String[][].class);
        } catch (Exception e) {
            return null;
        }
    }
}