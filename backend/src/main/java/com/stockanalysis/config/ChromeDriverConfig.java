package com.stockanalysis.config;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChromeDriverConfig {

    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverConfig.class);

    @Bean
    public WebDriver createChromeDriver() {
        try {
            WebDriverManager.chromedriver().browserVersion("133").setup();

            ChromeOptions options = new ChromeOptions();

            // ✅ Spoof User-Agent to appear as a real browser
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

            // ✅ Hide Selenium automation (removes "navigator.webdriver" property)
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("useAutomationExtension", false);

            // ✅ Allow cookies and normal browsing
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.default_content_setting_values.cookies", 1);
            options.setExperimentalOption("prefs", prefs);

            // ✅ Maintain existing Mac M3 compatibility options
            options.addArguments("--headless=new");  // Keep for headless mode on M3 Macs
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--enable-javascript");

            logger.info("Initializing Chrome WebDriver with anti-detection settings");
            WebDriver driver = new ChromeDriver(options);

            // ✅ Remove navigator.webdriver property to fully hide Selenium
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            return driver;

        } catch (Exception e) {
            logger.error("Failed to create Chrome WebDriver: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Chrome WebDriver", e);
        }
    }
}
