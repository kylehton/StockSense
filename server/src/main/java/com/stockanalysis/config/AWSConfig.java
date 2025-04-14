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

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${AWS_REGION}")
    private String region;

    @PostConstruct
    public void init() {
        System.out.println("===== AWS CONFIG START =====");
        System.out.println("AWS_ACCESS_KEY_ID: " + (accessKey != null ? "✅ Loaded" : "❌ MISSING"));
        System.out.println("AWS_SECRET_ACCESS_KEY: " + (secretKey != null ? "✅ Loaded" : "❌ MISSING"));
        System.out.println("AWS_REGION: " + (region != null ? "✅ Loaded" : "❌ MISSING"));
        System.out.println("AWS_BUCKET_NAME: " + (bucketName != null ? "✅ Loaded" : "❌ MISSING"));
        System.out.println("===== AWS CONFIG END =====");
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String getBucketName() {
        return bucketName;
    }
}
