package com.he180773.testreact.controller.client;

import com.he180773.testreact.dto.GeminiResponse;
import com.he180773.testreact.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173", "https://lienhoay.io.vn"},
    allowedHeaders = "*", 
    methods = {RequestMethod.POST, RequestMethod.OPTIONS, RequestMethod.GET}
)
public class GeminiController {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiController.class);
    
    private final GeminiService geminiService;
    
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }
    
    /**
     * Generate model image from user image
     * POST /api/gemini/generate-model
     */
    @PostMapping("/generate-model")
    public ResponseEntity<GeminiResponse> generateModelImage(
        @RequestParam("userImage") MultipartFile userImage
    ) {
        logger.info("Received request to generate model image");
        GeminiResponse response = geminiService.generateModelImage(userImage);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate virtual try-on image
     * POST /api/gemini/virtual-try-on
     */
    @PostMapping("/virtual-try-on")
    public ResponseEntity<GeminiResponse> generateVirtualTryOn(
        @RequestParam("modelImageUrl") String modelImageUrl,
        @RequestParam("garmentImage") MultipartFile garmentImage
    ) {
        logger.info("Received request for virtual try-on");
        GeminiResponse response = geminiService.generateVirtualTryOn(modelImageUrl, garmentImage);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate mix-match image
     * POST /api/gemini/mix-match
     */
    @PostMapping("/mix-match")
    public ResponseEntity<GeminiResponse> generateMixMatch(
        @RequestParam("modelImageUrl") String modelImageUrl,
        @RequestParam(value = "topImage", required = false) MultipartFile topImage,
        @RequestParam(value = "bottomImage", required = false) MultipartFile bottomImage,
        @RequestParam(value = "accessories", required = false) MultipartFile accessories,
        @RequestParam(value = "fullBodyImage", required = false) MultipartFile fullBodyImage
    ) {
        logger.info("Received request for mix-match");
        GeminiResponse response = geminiService.generateMixMatch(
            modelImageUrl, topImage, bottomImage, accessories, fullBodyImage
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate pose variation
     * POST /api/gemini/pose-variation
     */
    @PostMapping("/pose-variation")
    public ResponseEntity<GeminiResponse> generatePoseVariation(
        @RequestParam("tryOnImageUrl") String tryOnImageUrl,
        @RequestParam("poseInstruction") String poseInstruction
    ) {
        logger.info("Received request for pose variation: {}", poseInstruction);
        GeminiResponse response = geminiService.generatePoseVariation(tryOnImageUrl, poseInstruction);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test endpoint to verify backend configuration
     * GET /api/gemini/test-config
     */
    @GetMapping("/test-config")
    public ResponseEntity<java.util.Map<String, Object>> testConfig() {
        logger.info("Testing Gemini configuration");
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        String apiKey = geminiService.getApiKey();
        boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
        
        response.put("hasApiKey", hasApiKey);
        
        if (hasApiKey) {
            String maskedKey = apiKey.substring(0, Math.min(10, apiKey.length())) + 
                             "..." + 
                             apiKey.substring(Math.max(0, apiKey.length() - 4));
            response.put("apiKeyPreview", maskedKey);
            response.put("apiKeyLength", apiKey.length());
        }
        
        response.put("apiUrl", geminiService.getApiUrl());
        response.put("expectedKeyLength", "~39 characters");
        response.put("expectedKeyPrefix", "AIzaSy");
        
        boolean validFormat = hasApiKey && apiKey.startsWith("AIzaSy") && apiKey.length() >= 35;
        response.put("validFormat", validFormat);
        
        response.put("status", validFormat ? "✅ Configuration looks good" : "❌ Configuration issues detected");
        
        return ResponseEntity.ok(response);
    }
}