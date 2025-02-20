package com.stockanalysis;

import com.stockanalysis.scraper.WebScraper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;



public class SeleniumConnectionTest {

    private WebDriver driver;
    private WebScraper webScraper;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
    }

    @Test
    public void testSeleniumConnection() {
        driver.get("https://www.google.com");
        assertEquals("Google", driver.getTitle(), "ChromeDriver is not working properly.");
        System.out.println("Selenium successfully connected to Chrome.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
