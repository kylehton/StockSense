package com.stockanalysis.service;

import com.stockanalysis.config.AWSConfig;


import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private final S3Client s3Client;
    private String bucketName;

    @Autowired
    public S3Service(AWSConfig awsConfig) {
        // Load environment variables using dotenv if not already set
        this.s3Client = awsConfig.buildS3Client();
        this.bucketName = awsConfig.getBucketName();
    }

    public void uploadScrapedData(String key, Object data) {
        try {
            // Convert object to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(data);

            // Upload JSON to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonData, StandardCharsets.UTF_8));

            System.out.println("Successfully uploaded to S3: " + bucketName + "/" + key);
        } catch (IOException e) {
            System.err.println("Error converting data to JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error uploading to S3: " + e.getMessage());
        }
    }
}
