package com.stockanalysis.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class ChromeDriverConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverConfig.class);

    @Bean
    public WebDriver createChromeDriver() {
        try {
            WebDriverManager.chromedriver().browserVersion("133").setup();
            
            ChromeOptions options = new ChromeOptions();
            
            // Essential options only for M3 Mac compatibility
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--enable-javascript");
            
            logger.info("Initializing Chrome WebDriver with minimal options");
            return new ChromeDriver(options);
            
        } catch (Exception e) {
            logger.error("Failed to create Chrome WebDriver: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Chrome WebDriver", e);
        }
    }
}