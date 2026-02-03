package com.he180773.testreact.exception;

/**
 * Exception thrown when network errors occur calling Gemini API
 * Results in 503 Service Unavailable response
 */
public class NetworkException extends RuntimeException {
    
    public NetworkException(String message) {
        super(message);
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
