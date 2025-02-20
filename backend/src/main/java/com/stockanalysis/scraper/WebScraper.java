package com.stockanalysis.scraper;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class WebScraper {
    private WebDriver driver;
    private WebDriverWait wait;

    public WebScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode (no GUI)
        options.addArguments("--disable-gpu"); // Disable GPU acceleration
        options.addArguments("--no-sandbox"); // Improve security
        options.addArguments("--disable-dev-shm-usage"); // Prevent crashes
        options.addArguments("--enable-javascript"); // Explicitly enable JS
        options.addArguments("--remote-debugging-port=9222"); // Debugging option

        // Add stealth options to prevent blocking of webscraper
        options.addArguments("--disable-blink-features=AutomationControlled"); // Disable automation flag to block detection
        options.addArguments("user-agent=Mozilla/5.0"); // Set a std. user agent


        // Initialize WebDriver (Chromium-based browsers)
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Max wait time
    }

    public String scrapeWebsite(String url) {
        try {
            driver.get(url);

            // Wait for the page to load (Wait for any visible element inside <body>)
            wait.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.tagName("body")));

            // Extract page source after JavaScript execution
            String pageSource = driver.getPageSource();

            // Parse with Jsoup
            Document doc = Jsoup.parse(pageSource);
            doc.select("a").remove(); // Remove all links

            // extracts text within div with class "article" if it exists
            String articleText = doc.select("div.article").text();
            if (articleText.isEmpty()) {
                articleText = doc.select("article").text();
            }

            return articleText.isEmpty() ? doc.text() : articleText;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error scraping website: " + e.getMessage();
        }
    }

    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
