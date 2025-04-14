package com.stockanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {
    private final String bucketName;
    private final String accessKey;
    private final String secretKey;
    private final String region;

    public AWSConfig(
        @Value("${AWS_BUCKET_NAME:}") String bucketName,
        @Value("${AWS_ACCESS_KEY:}") String accessKey,
        @Value("${AWS_SECRET_ACCESS_KEY:}") String secretKey,
        @Value("${AWS_REGION:}") String region
    ) {
        this.bucketName = bucketName;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }

    @PostConstruct
    public void log() {
        System.out.println("=== AWS CONFIG ===");
        System.out.println("Bucket: " + bucketName);
        System.out.println("Key: " + (accessKey.isEmpty() ? "❌" : "✅"));
        System.out.println("Secret: " + (secretKey.isEmpty() ? "❌" : "✅"));
        System.out.println("Region: " + region);
        System.out.println("===================");
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build();
    }

    public String getBucketName() {
        return bucketName;
    }
}
