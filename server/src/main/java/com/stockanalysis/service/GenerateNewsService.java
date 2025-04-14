package com.stockanalysis.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GenerateNewsService {

    @Value("${AV_API_KEY}")
    private String api_key;

    double getBestSentimentScoreForTicker(JSONArray tickerArray, String stockSymbol) {
        double bestScore = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < tickerArray.length(); i++) {
            JSONObject tickerObj = tickerArray.getJSONObject(i);
            if (tickerObj.getString("ticker").equalsIgnoreCase(stockSymbol)) {
                double relevance = Double.parseDouble(tickerObj.getString("relevance_score"));
                if (relevance >= 0.4) {
                    double sentiment = tickerObj.getDouble("ticker_sentiment_score");
                    bestScore = Math.max(bestScore, sentiment);
                }
            }
        }
        return bestScore;
    }
    
    // Add a method to get the relevance score for a ticker
    double getRelevanceScoreForTicker(JSONArray tickerArray, String stockSymbol) {
        for (int i = 0; i < tickerArray.length(); i++) {
            JSONObject tickerObj = tickerArray.getJSONObject(i);
            if (tickerObj.getString("ticker").equalsIgnoreCase(stockSymbol)) {
                return Double.parseDouble(tickerObj.getString("relevance_score"));
            }
        }
        return 0.0;
    }
    
    public String[][] generateNews(String stockSymbol) {

        String[][] newsInfo = new String[4][5];

        
        // Alpha Vantage API URL
        String apiCallURL = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=" + stockSymbol + "&apikey=" + api_key;
                        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiCallURL))
                .header("User-Agent", "Mozilla/5.0") // Avoid getting blocked
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            // Extract the "news" array
            JSONArray newsArray = jsonResponse.getJSONArray("feed");

            // Arrays to store the articles and their scores
            List<JSONObject> articles = new ArrayList<>();
            List<Double> relevanceScores = new ArrayList<>();

            // Loop through all news items and collect those relevant to the stock symbol
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject newsItem = newsArray.getJSONObject(i);
                JSONArray tickerArray = newsItem.getJSONArray("ticker_sentiment");
                
                // Get the relevance score for this ticker
                double relevanceScore = getRelevanceScoreForTicker(tickerArray, stockSymbol);
                
                if (relevanceScore >= 0.4) {
                    articles.add(newsItem);
                    relevanceScores.add(relevanceScore);
                }
            }
            
            // Create a list of indices to sort
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < articles.size(); i++) {
                indices.add(i);
            }
            
            // Sort indices by relevance score (descending)
            Collections.sort(indices, (a, b) -> Double.compare(relevanceScores.get(b), relevanceScores.get(a)));
            
            // Take the top 5 or fewer if less are available
            int count = Math.min(5, articles.size());
            
            // Populate the newsInfo array with the top articles
            for (int i = 0; i < count; i++) {
                int index = indices.get(i);
                JSONObject article = articles.get(index);
                double sentimentScore = getBestSentimentScoreForTicker(article.getJSONArray("ticker_sentiment"), stockSymbol);
                
                newsInfo[0][i] = article.getString("title");
                newsInfo[1][i] = article.getString("source");
                newsInfo[2][i] = article.getString("url");
                newsInfo[3][i] = String.valueOf(sentimentScore);
            }
            
            // Fill remaining slots with null if less than 5 items
            for (int i = count; i < 5; i++) {
                newsInfo[0][i] = null;
                newsInfo[1][i] = null;
                newsInfo[2][i] = null;
                newsInfo[3][i] = null;
            }
            
            // Print the results
            for (int x = 0; x < count; x++) {
                System.out.println("Title: " + newsInfo[0][x]);
                System.out.println("Source: " + newsInfo[1][x]);
                System.out.println("Link: " + newsInfo[2][x]);
                System.out.println("Sentiment Score: " + newsInfo[3][x]);
                System.out.println("Relevance Score: " + relevanceScores.get(indices.get(x)));
            }
            
            return newsInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("No API Response");
        }
    }
}