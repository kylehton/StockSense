package com.stockanalysis.service;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private final S3Client s3Client;

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    @Autowired
    public S3Service() {
        // Load environment variables using dotenv if not already set
        Dotenv dotenv = Dotenv.load();
        
        // Set the environment variables
        this.accessKey = dotenv.get("AWS_ACCESS_KEY");
        this.secretKey = dotenv.get("AWS_SECRET_KEY");
        this.region = dotenv.get("AWS_REGION");
        this.bucketName = dotenv.get("AWS_BUCKET_NAME");

        // Initialize S3 client with credentials
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        s3Client.listBuckets();
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
                    .contentType("application/json")
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
