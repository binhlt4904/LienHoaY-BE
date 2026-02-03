package com.he180773.testreact.exception;

/**
 * Exception thrown when image validation fails
 * Results in 400 Bad Request response
 */
public class InvalidImageException extends RuntimeException {
    
    public InvalidImageException(String message) {
        super(message);
    }
    
    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
