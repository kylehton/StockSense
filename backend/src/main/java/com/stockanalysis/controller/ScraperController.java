package com.stockanalysis.controller;

import com.stockanalysis.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ScraperController {

    private final ScraperService scraperService;

    @Autowired
    public ScraperController( ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @GetMapping("/scrape")
    public String scrapeWebsite(@RequestParam String url) {
        String completedText = scraperService.scrape(url);
        return completedText;
    }
}
