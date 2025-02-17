package com.stockanalysis.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GenerateNewsService {

    
    public String[][] generateNews(String stockSymbol) {

        String[][] newsInfo = new String[3][5];
        // Placeholder for news generation
        String apiCallURL = "https://query1.finance.yahoo.com/v1/finance/search?q=" + stockSymbol;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiCallURL))
                .header("User-Agent", "Mozilla/5.0") // Avoid getting blocked
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            JSONObject jsonResponse = new JSONObject(response.body());

        // Extract the "news" array
            JSONArray newsArray = jsonResponse.getJSONArray("news");

            // Loop through the news items and print title, publisher, and link
            for (int i = 0; i < 5; i++) {
                JSONObject newsItem = newsArray.getJSONObject(i);
                newsInfo[0][i] = newsItem.getString("title");
                newsInfo[1][i] = newsItem.getString("publisher");
                newsInfo[2][i] = newsItem.getString("link");

            }
            System.out.println(newsInfo[0][0]);
            System.out.println(newsInfo[1][0]);
            System.out.println(newsInfo[2][0]);
            return newsInfo;
        } catch (Exception e) {
            e.printStackTrace();
                throw new IllegalArgumentException("No API Response");
        }
    }
}
