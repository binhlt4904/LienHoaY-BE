package com.he180773.testreact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiResponse {
    
    private String imageUrl;
    private String imageData;  // Base64 encoded
    private String contentType;
    private Map<String, Object> metadata;
    private String error;
    
    public GeminiResponse() {
    }
    
    public GeminiResponse(String imageData, String contentType) {
        this.imageData = imageData;
        this.contentType = contentType;
    }
    
    public GeminiResponse(String error) {
        this.error = error;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImageData() {
        return imageData;
    }
    
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
