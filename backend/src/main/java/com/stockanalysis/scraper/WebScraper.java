package com.stockanalysis.scraper;

import com.stockanalysis.config.ChromeDriverConfig;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class WebScraper {
    private WebDriver driver;
    private WebDriverWait wait;

    public WebScraper(ChromeDriverConfig chromeDriverConfig) {
        this.driver = chromeDriverConfig.createChromeDriver();
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
