package com.he180773.testreact.exception;

import com.he180773.testreact.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GeminiExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiExceptionHandler.class);
    
    /**
     * Handle InvalidImageException - 400 Bad Request
     */
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImage(
        InvalidImageException ex, 
        HttpServletRequest request
    ) {
        logger.error("Invalid image error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Invalid image",
            ex.getMessage(),
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle GeminiApiException - 502 Bad Gateway
     */
    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<ErrorResponse> handleGeminiApiError(
        GeminiApiException ex, 
        HttpServletRequest request
    ) {
        logger.error("Gemini API error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Gemini API error",
            ex.getMessage(),
            request.getRequestURI(),
            HttpStatus.BAD_GATEWAY.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }
    
    /**
     * Handle NetworkException - 503 Service Unavailable
     */
    @ExceptionHandler(NetworkException.class)
    public ResponseEntity<ErrorResponse> handleNetworkError(
        NetworkException ex, 
        HttpServletRequest request
    ) {
        logger.error("Network error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Service unavailable",
            ex.getMessage(),
            request.getRequestURI(),
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Handle MaxUploadSizeExceededException - 400 Bad Request
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
        MaxUploadSizeExceededException ex, 
        HttpServletRequest request
    ) {
        logger.error("File size exceeded: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "File too large",
            "Maximum upload size exceeded. Please upload a smaller file.",
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle generic Exception - 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
        Exception ex, 
        HttpServletRequest request
    ) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Internal server error",
            "An unexpected error occurred: " + ex.getMessage(),
            request.getRequestURI(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
