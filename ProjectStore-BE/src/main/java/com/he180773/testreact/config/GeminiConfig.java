package com.he180773.testreact.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {
    
    private String apiKey;
    private String apiUrl = "https://generativelanguage.googleapis.com/v1beta";
    private String modelName = "gemini-3-pro-image-preview";
    
    @PostConstruct
    public void validateConfig() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "Gemini API key is not configured. Please set 'gemini.api-key' in application.properties " +
                "or set GEMINI_API_KEY environment variable."
            );
        }
        
        if (!apiKey.startsWith("AIzaSy")) {
            System.err.println("WARNING: Gemini API key format may be invalid. Expected format starts with 'AIzaSy'");
        }
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
