package com.stockanalysis.scraper;

import com.stockanalysis.config.ChromeDriverConfig;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class WebScraper {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Patterns that should be used for CSS selectors (without special characters)
    private static final List<String> SELECTOR_PATTERNS = Arrays.asList(
        "nav", "footer", "aside", "header", "advertisement", "promo", "newsletter", 
        "social", "related", "subscribe", "cookie", "premium", "sidebar", 
        "disclaimer", "metadata", "navigation", "menu", "topbar", "navbar", 
        "banner", "ad", "ads", "paywall", "subscription", "login", "signin", 
        "signup", "search", "ticker", "marketdata", "accessibility", "help", 
        "user", "profile", "tools", "service", "summary", "breadcrumbs", 
        "categories", "tags", "sponsored", "share", "comments", "author"
    );
    
    // Patterns that should be used for text content matching (can include special characters)
    private static final List<String> TEXT_PATTERNS = Arrays.asList(
        "join", "become a member", "premium investing", "sign up", "log in", 
        "free article", "premium services", "investor alert", "best stocks", 
        "disclosure policy", "current price", "today's change", "positions in", 
        "get stock recommendations", "portfolio guidance", "invest better",
        "disclosure", "has no position", "stock mentioned", "stock advisor",
        "motley fool", "seeking alpha", "yahoo finance", "investor place", 
        "barron", "bloomberg", "cnbc", "reuters", "marketwatch", "investing.com"
    );

    public WebScraper(ChromeDriverConfig chromeDriverConfig) {
        this.driver = chromeDriverConfig.createChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Max wait time
    }

    public String scrapeWebsite(String url) {
        try {
            driver.get(url);

            // Wait for page to fully load
            wait.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.tagName("body")));

            // Extract HTML after JavaScript execution
            String pageSource = driver.getPageSource();

            // Parse with Jsoup
            Document doc = Jsoup.parse(pageSource);

            // Remove unwanted elements
            removeUnwantedElements(doc);

            // Extract the most relevant text block
            String articleText = extractArticleContent(doc);

            // Final cleanup: remove metadata, stock prices, disclaimers
            return cleanFinalText(articleText);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error scraping website: " + e.getMessage();
        }
    }

    /**
     * Removes unwanted elements like navigation, sidebars, ads, footers, etc.
     */
    private void removeUnwantedElements(Document doc) {
        // Generate CSS selectors from the patterns
        Set<String> selectors = new HashSet<>();
        
        for (String pattern : SELECTOR_PATTERNS) {
            // Add pattern as class selector
            selectors.add("." + pattern);
            
            // Add pattern as id selector
            selectors.add("#" + pattern);
            
            // Add common variations
            selectors.add("div." + pattern);
            selectors.add("section." + pattern);
            selectors.add("div#" + pattern);
        }
        
        // Add common HTML5 semantic tags
        selectors.addAll(Arrays.asList("nav", "footer", "aside", "header"));
        
        // Remove elements matching selectors
        for (String selector : selectors) {
            try {
                doc.select(selector).remove();
            } catch (Exception e) {
                // Log the error but continue with other selectors
                System.err.println("Error applying selector '" + selector + "': " + e.getMessage());
            }
        }
        
        // Remove elements containing unwanted text patterns
        removeElementsByTextContent(doc);
    }
    
    /**
     * Removes elements containing specific promotional or navigation text
     */
    private void removeElementsByTextContent(Document doc) {
        Elements allElements = doc.select("*");
        for (Element element : allElements) {
            String text = element.ownText().toLowerCase();
            if (text.isEmpty()) {
                continue;
            }
            
            for (String pattern : TEXT_PATTERNS) {
                if (text.contains(pattern.toLowerCase())) {
                    element.remove();
                    break;
                }
            }
        }
    }

    /**
     * Extracts the most relevant article text dynamically.
     * Works across various financial news websites.
     */
    private String extractArticleContent(Document doc) {
        String text = "";

        // Common article containers across financial news sites
        List<String> possibleContainers = Arrays.asList(
            // General-purpose article containers
            "div.article-content", "div.article-body", "div.article-text", 
            "div.article", "article", "section.main-content", "div.entry-content",
            "div.post-content", "main", "div#article-body", "div.content-block",
            "div.article-container", "div.content-area", "div.story-content",
            
            // Financial site-specific but generalized
            ".main-content", ".post", ".article-wrapper", "#content-body",
            "div[itemprop='articleBody']", ".story", ".news-content",
            ".news-article", ".content-article", ".investing-content",
            ".market-content", ".finance-article", "[data-component='text-block']"
        );

        for (String container : possibleContainers) {
            try {
                Element content = doc.selectFirst(container);
                if (content != null) {
                    // Extract only paragraph text to avoid headers, captions, etc.
                    Elements paragraphs = content.select("p");
                    if (!paragraphs.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Element p : paragraphs) {
                            String pText = p.text().trim();
                            if (!pText.isEmpty() && pText.length() > 20) {  // Skip very short paragraphs
                                sb.append(pText).append("\n\n");
                            }
                        }
                        text = sb.toString().trim();
                        break;
                    } else {
                        text = content.text();
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error with container selector '" + container + "': " + e.getMessage());
            }
        }

        // If no container worked, select the largest text block
        if (text.isEmpty()) {
            text = getLargestTextBlock(doc);
        }

        return text;
    }

    /**
     * Selects the largest text block dynamically, with improvements for finding actual content
     */
    private String getLargestTextBlock(Document doc) {
        Element largestBlock = null;
        int maxWords = 0;

        // Focus on elements likely to contain article content
        Elements contentContainers = doc.select("div, section, article, main");
        
        for (Element element : contentContainers) {
            // Skip elements with class/id suggesting they're not main content
            if (elementHasUnwantedClassOrId(element)) {
                continue;
            }
            
            // Count paragraphs - articles typically have multiple paragraphs
            Elements paragraphs = element.select("p");
            if (paragraphs.size() < 3) {
                continue; // Skip if fewer than 3 paragraphs
            }
            
            // Check total text length
            String content = element.text();
            int wordCount = content.split("\\s+").length;
            
            // Bonus points for elements with more paragraphs (likely articles)
            wordCount += paragraphs.size() * 5;
            
            if (wordCount > maxWords) {
                maxWords = wordCount;
                largestBlock = element;
            }
        }

        // If found a good candidate, extract just the paragraph text
        if (largestBlock != null) {
            Elements paragraphs = largestBlock.select("p");
            if (!paragraphs.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Element p : paragraphs) {
                    String pText = p.text().trim();
                    if (!pText.isEmpty() && pText.length() > 20) {  // Skip very short paragraphs
                        sb.append(pText).append("\n\n");
                    }
                }
                return sb.toString().trim();
            }
            return largestBlock.text();
        }
        
        return "";
    }
    
    /**
     * Checks if element has class or id suggesting it's not main content
     */
    private boolean elementHasUnwantedClassOrId(Element element) {
        String classNames = element.attr("class").toLowerCase();
        String id = element.attr("id").toLowerCase();
        
        for (String pattern : SELECTOR_PATTERNS) {
            pattern = pattern.toLowerCase();
            if (classNames.contains(pattern) || id.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Cleans up remaining metadata, stock tickers, and disclaimers.
     * Works for various financial news sites.
     */
    private String cleanFinalText(String text) {
        // Remove stock tickers with more comprehensive patterns
        text = text.replaceAll("\\((?:[A-Z]{1,5})\\s*[-+]?\\d+(?:\\.\\d+)?%?\\)", ""); 
        text = text.replaceAll("(?:[A-Z]{1,5})\\s*[-+]?\\d+(?:\\.\\d+)?%?", "");
        
        // Remove ticker symbols with financial notation
        text = text.replaceAll("(?:NASDAQ|NYSE|DOW|S&P|FTSE|NIKKEI):\\s*[A-Z]+", "");
        
        // Remove stock prices and financial metrics
        text = text.replaceAll("\\$\\d+(?:\\.\\d+)?(?:\\s*[BMK])?", ""); // Remove $123.45B
        text = text.replaceAll("[-+]?\\d+(?:\\.\\d+)?%", ""); // Remove percentage changes
        
        // Remove date/time patterns common in financial articles
        text = text.replaceAll("(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{1,2}(?:,\\s+\\d{4})?", "");
        text = text.replaceAll("\\d{1,2}:\\d{2}(?:a\\.m\\.|p\\.m\\.|AM|PM|ET)", "");
        
        // Remove any line starting with a common pattern
        StringBuilder cleanedText = new StringBuilder();
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Skip short lines (likely headers or metadata)
            if (trimmedLine.length() < 20) {
                continue;
            }
            
            // Skip lines starting with common patterns
            boolean skip = false;
            for (String pattern : TEXT_PATTERNS) {
                if (trimmedLine.toLowerCase().startsWith(pattern.toLowerCase())) {
                    skip = true;
                    break;
                }
            }
            
            if (!skip) {
                cleanedText.append(trimmedLine).append("\n\n");
            }
        }
        
        // Final cleanup of whitespace
        text = cleanedText.toString()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
        
        return text;
    }

    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}