package com.stockanalysis.controller;

import com.stockanalysis.service.S3Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WebScraperController {

    private final S3Service s3Service = new S3Service();

    @GetMapping("/upload")
    public String uploadData() {
        // Sample scraped data
        Map<String, Object> scrapedData = Map.of(
            "symbol", "TSLA",
            "news", List.of(
                Map.of("headline", "Tesla unveils new car", "sentiment", 0.9),
                Map.of("headline", "Tesla shares drop", "sentiment", -0.5)
            )
        );

        // Upload JSON to S3
        String objectKey = "scraped_data.json";
        s3Service.uploadScrapedData(objectKey, scrapedData);

        return "Upload successful!";
    }
}
