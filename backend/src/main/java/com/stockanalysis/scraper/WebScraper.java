package com.stockanalysis.scraper;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Component
public class WebScraper {
    

    // Separate function to handle the scraping
    public String scrapeWebsite(String url) {
        // Set path to chromedriver
        String driverPath = System.getProperty("user.dir") + File.separator + "drivers" + File.separator + "chromedriver";
        System.setProperty("webdriver.chrome.driver", driverPath);
        
        // Initialize WebDriver and open the page
        WebDriver driver = new ChromeDriver();
        driver.get(url);

        // Get the page source after JavaScript execution
        String pageSource = driver.getPageSource();
        driver.quit();

        // Parse the loaded HTML with Jsoup
        Document doc = Jsoup.parse(pageSource);
        return doc.text(); // Return extracted text
    }
}
