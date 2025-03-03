package com.stockanalysis.scraper;

import com.stockanalysis.config.ChromeDriverConfig;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class WebScraper {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Yahoo Finance specific CSS selectors - most reliable first
    private static final List<String> YAHOO_FINANCE_ARTICLE_SELECTORS = Arrays.asList(
        "div[data-test-locator='articleBody']",
        ".caas-body", 
        ".article-body"
    );
    
    // Minimal list of patterns to exclude
    private static final List<String> SELECTOR_PATTERNS = Arrays.asList(
        "nav", "footer", "header", "ad", "promo", "sidebar"
    );

    public WebScraper(ChromeDriverConfig chromeDriverConfig) {
        this.driver = chromeDriverConfig.createChromeDriver();
        // Shorter wait time for better performance
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5)); 
    }

    public String scrapeWebsite(String url) {
        try {
            // Tighter timeouts
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
            
            driver.get(url);

            // Single quick scroll to trigger initial content load
            fastScroll();
            
            // Wait for any Yahoo Finance article selector
            for (String selector : YAHOO_FINANCE_ARTICLE_SELECTORS) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                    break;
                } catch (Exception e) {
                    // Continue with next selector
                }
            }

            // Extract HTML
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            // Faster cleaning
            fastClean(doc);

            // Try Yahoo Finance selectors first, then fallback
            String content = extractContent(doc);
            
            return content.trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error scraping website: " + e.getMessage();
        }
    }
    
    /**
     * Quick single scroll to trigger content loading
     */
    private void fastScroll() {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            // Scroll down 70% of page height to trigger loading
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight * 0.7)");
            // Brief pause for content to load
            Thread.sleep(500);
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }

    /**
     * Fast minimal cleaning of unwanted elements
     */
    private void fastClean(Document doc) {
        // Remove only the most problematic elements
        Set<String> selectors = new HashSet<>();
        
        for (String pattern : SELECTOR_PATTERNS) {
            selectors.add("." + pattern);
            selectors.add("#" + pattern);
        }
        
        // Yahoo Finance specific elements (minimal list)
        selectors.addAll(Arrays.asList(
            ".caas-carousel",
            ".caas-readmore",
            ".video-wrapper",
            ".caas-figure",
            ".caas-img-container",
            "[data-test-locator='recommended-articles']"
        ));
        
        for (String selector : selectors) {
            try {
                doc.select(selector).remove();
            } catch (Exception e) {
                // Continue
            }
        }
    }

    /**
     * Extracts content with minimal processing
     */
    private String extractContent(Document doc) {
        // Try Yahoo selectors first
        for (String selector : YAHOO_FINANCE_ARTICLE_SELECTORS) {
            Element content = doc.selectFirst(selector);
            if (content != null) {
                // Get paragraphs
                Elements paragraphs = content.select("p");
                if (!paragraphs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Element p : paragraphs) {
                        String pText = p.text().trim();
                        if (!pText.isEmpty()) {
                            sb.append(pText).append("\n\n");
                        }
                    }
                    String result = sb.toString().trim();
                    if (!result.isEmpty() && result.length() > 50) {
                        return result;
                    }
                }
                
                // If no paragraphs, use full text
                String fullText = content.text().trim();
                if (!fullText.isEmpty()) {
                    return fullText;
                }
            }
        }

        // Fast fallback - try common containers
        for (String container : Arrays.asList("article", "main", ".content", ".article")) {
            Element content = doc.selectFirst(container);
            if (content != null) {
                Elements paragraphs = content.select("p");
                if (!paragraphs.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Element p : paragraphs) {
                        String pText = p.text().trim();
                        if (!pText.isEmpty()) {
                            sb.append(pText).append("\n\n");
                        }
                    }
                    return sb.toString().trim();
                }
                return content.text();
            }
        }

        // Last resort - largest text block
        Element largest = null;
        int maxLen = 0;
        for (Element div : doc.select("div")) {
            String text = div.text();
            if (text.length() > maxLen) {
                maxLen = text.length();
                largest = div;
            }
        }
        
        return largest != null ? largest.text() : "No content found";
    }

    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}