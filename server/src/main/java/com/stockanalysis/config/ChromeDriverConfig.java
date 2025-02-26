package com.stockanalysis.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromeDriverConfig {

    private WebDriver driver;

    @Bean
    public WebDriver createChromeDriver() {
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
        return this.driver;
    }
}