package com.he180773.testreact.exception;

/**
 * Exception thrown when Gemini API returns an error
 * Results in 502 Bad Gateway response
 */
public class GeminiApiException extends RuntimeException {
    
    public GeminiApiException(String message) {
        super(message);
    }
    
    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
