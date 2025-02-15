package com.stockanalysis.service;

import com.stockanalysis.scraper.WebScraper;
import com.stockanalysis.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class ScraperService {

    private final WebScraper webScraper;
    private final S3Service s3Service;

    // Inject WebScraper and S3Service
    @Autowired
    public ScraperService(WebScraper webScraper, S3Service s3Service) {
        this.webScraper = webScraper;
        this.s3Service = s3Service;
    }

    public String scrape(String url) {
        // Scrape the website
        String scrapedData = webScraper.scrapeWebsite(url);

        // Generate a unique filename
        String objectKey = "scraped_data/" + UUID.randomUUID() + ".json";

        // Upload the scraped data to S3
        try{
          s3Service.uploadScrapedData(objectKey, scrapedData);  
        }
        catch(Exception e){
          return "Error uploading to S3: " + e.getMessage();
        }

        // Return the S3 URL of the uploaded file
        return "Scraped data uploaded to S3: https://s3.amazonaws.com/news-watchlist-links/" + objectKey + "\t\n\n" + scrapedData;
    }
}
