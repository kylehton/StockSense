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
    private WebDriver driver;

    // Constructor to initialize WebDriver once
    public WebScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Ensure headless mode is enabled
        options.addArguments("--disable-gpu"); // Disable GPU acceleration
        options.addArguments("--no-sandbox"); // Improve performance
        options.addArguments("--disable-dev-shm-usage"); // Prevent crashes
        options.addArguments("--remote-debugging-port=9222"); // Optionally, to debug
        options.addArguments("--enable-javascript"); // Explicitly enable JS
        options.addArguments("--guest"); // run as guest


        // Set path to chromedriver (Optional: Use Selenium Manager)
        String driverPath = System.getProperty("user.dir") + File.separator + "drivers" + File.separator + "chromedriver";
        System.setProperty("webdriver.chrome.driver", driverPath);

        // Initialize WebDriver once
        this.driver = new ChromeDriver(options);
    }

    public String scrapeWebsite(String url) {
        // Open the page and get content
        driver.get(url);
        
        // Wait for JavaScript to load (may need to adjust wait time)
        try {
            Thread.sleep(5000); // 5 seconds for JS rendering (use WebDriverWait for better performance)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String pageSource = driver.getPageSource();

        // Parse the loaded HTML with Jsoup
        Document doc = Jsoup.parse(pageSource);
        doc.select("a").remove();  // This removes all <a> tags (links)

        return doc.text();  // Extract text content
    }

    // Cleanup WebDriver (Call this when shutting down the app)
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
