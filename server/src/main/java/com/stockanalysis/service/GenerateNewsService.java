package com.stockanalysis.service;

import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GenerateNewsService {

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
    
    
    
    public String[][] generateNews(String stockSymbol) {

        Dotenv dotenv = Dotenv.load();

        String[][] newsInfo = new String[4][5];
        String api_key = dotenv.get("AV_API_KEY");
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

            // Loop through the news items and print title, publisher, and link
            // Store the first 5 items in newsInfo
            // Columns are: Article Title (0), Publisher (1), Link (2), Sentiment Score (3)
            int newsCount = 0;
            int increment = 0;
            while (newsCount < 5 && increment < newsArray.length()) {
                JSONObject newsItem = newsArray.getJSONObject(increment);
                System.out.println(newsItem);
                // checks whether the article is truly about the stock symbol company, not just mentions it
                JSONArray tickerArray = newsItem.getJSONArray("ticker_sentiment");
                double score = getSentimentScoreForTicker(tickerArray, stockSymbol);

                if (score >= 0.4) {
                    newsInfo[0][newsCount] = newsItem.getString("title");
                    newsInfo[1][newsCount] = newsItem.getString("source");
                    newsInfo[2][newsCount] = newsItem.getString("url");
                    newsInfo[3][newsCount] = String.valueOf(score);
                    newsCount++;
                }
                increment++;
            }
            for (int x = 0; x < 5; x++) {
                System.out.println("Title: " + newsInfo[0][x]);
                System.out.println("Source: " + newsInfo[1][x]);
                System.out.println("Link: " + newsInfo[2][x]);
                System.out.println("Sentiment Score: " + newsInfo[3][x]);
            }
            return newsInfo;
        } catch (Exception e) {
            e.printStackTrace();
                throw new IllegalArgumentException("No API Response");
        }
    }
}
