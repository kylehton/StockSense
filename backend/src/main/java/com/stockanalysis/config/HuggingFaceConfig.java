package com.stockanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.time.Duration;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class HuggingFaceConfig {

    Dotenv dotenv = Dotenv.load();
    
    
    private String apiKey = dotenv.get("HUGGINGFACE_API_KEY");
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    public String getHFKey() {
        return apiKey;
    }
}
