package com.stockanalysis.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    public AWSConfig() {
        Dotenv dotenv = Dotenv.load();
        
        // Set the environment variables
        this.accessKey = dotenv.get("AWS_ACCESS_KEY");
        this.secretKey = dotenv.get("AWS_SECRET_KEY");
        this.region = dotenv.get("AWS_REGION");
        this.bucketName = dotenv.get("AWS_BUCKET_NAME");

    }

    public S3Client buildS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public ComprehendClient buildComprehendClient() {
        return ComprehendClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String getBucketName() {
        return bucketName;
    }
}
