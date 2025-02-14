package com.stockanalysis.service;

import com.stockanalysis.scraper.WebScraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperService {

    private final WebScraper webScraper;

    // Constructor to inject WebScraper dependency
    @Autowired
    public ScraperService(WebScraper webScraper) {
        this.webScraper = webScraper;
    }

    // Method to scrape website content using WebScraper
    public String scrape(String url) {
        return webScraper.scrapeWebsite(url); // Call the scrapeWebsite method
    }
}
