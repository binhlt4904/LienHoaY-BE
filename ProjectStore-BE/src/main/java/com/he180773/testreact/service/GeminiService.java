package com.he180773.testreact.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.he180773.testreact.config.GeminiConfig;
import com.he180773.testreact.dto.GeminiResponse;
import com.he180773.testreact.exception.GeminiApiException;
import com.he180773.testreact.exception.InvalidImageException;
import com.he180773.testreact.exception.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
public class GeminiService {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private final GeminiConfig geminiConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public GeminiService(GeminiConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
        this.webClient = WebClient.builder()
            .baseUrl(geminiConfig.getApiUrl())
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(10 * 1024 * 1024)) // 10MB buffer for large image responses
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get API key (for testing purposes)
     */
    public String getApiKey() {
        return geminiConfig.getApiKey();
    }
    
    /**
     * Get API URL (for testing purposes)
     */
    public String getApiUrl() {
        return geminiConfig.getApiUrl();
    }
    
    /**
     * Validate image content type
     */
    public void validateImageContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image file is required");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException(
                "Invalid image type: " + contentType + ". Allowed types: " + ALLOWED_IMAGE_TYPES
            );
        }
    }
    
    /**
     * Convert MultipartFile to bytes
     */
    public byte[] convertMultipartFileToBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new InvalidImageException("Failed to read image file", e);
        }
    }
    
    /**
     * Encode image bytes to Base64
     */
    public String encodeImageToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * Generate model image from user image
     */
    public GeminiResponse generateModelImage(MultipartFile userImage) {
        validateImageContentType(userImage);
        
        byte[] imageBytes = convertMultipartFileToBytes(userImage);
        String base64Image = encodeImageToBase64(imageBytes);
        String mimeType = userImage.getContentType();
        
        String prompt = "You are an expert fashion photographer AI. Transform the person in this image into a full-body fashion model photo suitable for an e-commerce website. The background must be a clean, neutral studio backdrop (light gray, #f0f0f0). The person should have a neutral, professional model expression. Preserve the person's identity, unique features, and body type, but place them in a standard, relaxed standing model pose. The final image must be photorealistic. Return ONLY the final image.";
        
        Map<String, Object> requestBody = buildGeminiRequest(base64Image, mimeType, prompt);
        
        return callGeminiApi(requestBody);
    }
    
    /**
     * Generate virtual try-on image
     */
    public GeminiResponse generateVirtualTryOn(String modelImageUrl, MultipartFile garmentImage) {
        validateImageContentType(garmentImage);
        
        byte[] garmentBytes = convertMultipartFileToBytes(garmentImage);
        String base64Garment = encodeImageToBase64(garmentBytes);
        String garmentMimeType = garmentImage.getContentType();
        
        // Extract base64 from data URL if needed
        String modelBase64 = extractBase64FromDataUrl(modelImageUrl);
        String modelMimeType = extractMimeTypeFromDataUrl(modelImageUrl);
        
        String prompt = "You are an expert virtual try-on AI. You will be given a 'model image' and a 'garment image'. Your task is to create a new photorealistic image where the person from the 'model image' is wearing the clothing from the 'garment image'.\n\n" +
            "**Crucial Rules:**\n" +
            "1.  **Complete Garment Replacement:** You MUST completely REMOVE and REPLACE the clothing item worn by the person in the 'model image' with the new garment. No part of the original clothing (e.g., collars, sleeves, patterns) should be visible in the final image.\n" +
            "2.  **Preserve the Model:** The person's face, hair, body shape, and pose from the 'model image' MUST remain unchanged.\n" +
            "3.  **Preserve the Background:** The entire background from the 'model image' MUST be preserved perfectly.\n" +
            "4.  **Apply the Garment:** Realistically fit the new garment onto the person. It should adapt to their pose with natural folds, shadows, and lighting consistent with the original scene.\n" +
            "5.  **Output:** Return ONLY the final, edited image. Do not include any text.";
        
        Map<String, Object> requestBody = buildGeminiRequestWithMultipleImages(
            Arrays.asList(
                Map.of("data", modelBase64, "mimeType", modelMimeType),
                Map.of("data", base64Garment, "mimeType", garmentMimeType)
            ),
            prompt
        );
        
        return callGeminiApi(requestBody);
    }
    
    /**
     * Generate mix-match image
     */
    public GeminiResponse generateMixMatch(
        String modelImageUrl,
        MultipartFile topImage,
        MultipartFile bottomImage,
        MultipartFile accessories,
        MultipartFile fullBodyImage
    ) {
        List<Map<String, String>> images = new ArrayList<>();
        
        // Add model image
        String modelBase64 = extractBase64FromDataUrl(modelImageUrl);
        String modelMimeType = extractMimeTypeFromDataUrl(modelImageUrl);
        images.add(Map.of("data", modelBase64, "mimeType", modelMimeType));
        
        StringBuilder promptBuilder = new StringBuilder("You are an expert virtual try-on AI. You will be given a 'model image'");
        
        // Add full body image if provided
        if (fullBodyImage != null && !fullBodyImage.isEmpty()) {
            validateImageContentType(fullBodyImage);
            byte[] fullBodyBytes = convertMultipartFileToBytes(fullBodyImage);
            String base64FullBody = encodeImageToBase64(fullBodyBytes);
            images.add(Map.of("data", base64FullBody, "mimeType", fullBodyImage.getContentType()));
            promptBuilder.append(" and a 'full-body garment image'");
        } else {
            // Add top image if provided
            if (topImage != null && !topImage.isEmpty()) {
                validateImageContentType(topImage);
                byte[] topBytes = convertMultipartFileToBytes(topImage);
                String base64Top = encodeImageToBase64(topBytes);
                images.add(Map.of("data", base64Top, "mimeType", topImage.getContentType()));
                promptBuilder.append(" and a 'top garment image'");
            }
            
            // Add bottom image if provided
            if (bottomImage != null && !bottomImage.isEmpty()) {
                validateImageContentType(bottomImage);
                byte[] bottomBytes = convertMultipartFileToBytes(bottomImage);
                String base64Bottom = encodeImageToBase64(bottomBytes);
                images.add(Map.of("data", base64Bottom, "mimeType", bottomImage.getContentType()));
                promptBuilder.append(" and a 'bottom garment image'");
            }
        }
        
        // Add accessories if provided
        if (accessories != null && !accessories.isEmpty()) {
            validateImageContentType(accessories);
            byte[] accessoriesBytes = convertMultipartFileToBytes(accessories);
            String base64Accessories = encodeImageToBase64(accessoriesBytes);
            images.add(Map.of("data", base64Accessories, "mimeType", accessories.getContentType()));
            promptBuilder.append(" and an 'accessory image 1'");
        }
        
        promptBuilder.append(". Your task is to create a new photorealistic image where the person from the 'model image' is wearing the provided items.\n\n");
        promptBuilder.append("**Crucial Rules:**\n");
        promptBuilder.append("1.  **Item Application:** Apply the provided items to the person.\n");
        
        if (fullBodyImage != null && !fullBodyImage.isEmpty()) {
            promptBuilder.append("    - COMPLETE OUTFIT REPLACEMENT: Replace the person's ENTIRE outfit (top and bottom) with the 'full-body garment image' (e.g., dress, robe, jumpsuit, ao dai). Ensure it fits naturally.\n");
        } else {
            if (topImage != null && !topImage.isEmpty()) {
                promptBuilder.append("    - Replace the upper body clothing with the 'top garment image'.\n");
            }
            if (bottomImage != null && !bottomImage.isEmpty()) {
                promptBuilder.append("    - Replace the lower body clothing with the 'bottom garment image'.\n");
            }
            if ((topImage == null || topImage.isEmpty()) && (bottomImage != null && !bottomImage.isEmpty())) {
                promptBuilder.append("    - Keep the person's existing upper body clothing UNCHANGED.\n");
            }
            if ((topImage != null && !topImage.isEmpty()) && (bottomImage == null || bottomImage.isEmpty())) {
                promptBuilder.append("    - Keep the person's existing lower body clothing UNCHANGED.\n");
            }
        }
        
        if (accessories != null && !accessories.isEmpty()) {
            promptBuilder.append("    - Add or apply ALL provided accessory images naturally (e.g., handbag, hat, jewelry, glasses) at their appropriate locations.\n");
        }
        
        promptBuilder.append("2.  **Preserve the Model:** The person's face, hair, body shape, and pose MUST remain unchanged (unless an accessory, like a hat, naturally covers part of the hair).\n");
        promptBuilder.append("3.  **Preserve the Background:** The background MUST be preserved perfectly.\n");
        promptBuilder.append("4.  **Realistic Fit:** The items should fit naturally, respecting gravity, layering, and lighting.\n");
        promptBuilder.append("5.  **Output:** Return ONLY the final, edited image.");
        
        Map<String, Object> requestBody = buildGeminiRequestWithMultipleImages(images, promptBuilder.toString());
        
        return callGeminiApi(requestBody);
    }
    
    /**
     * Generate pose variation
     */
    public GeminiResponse generatePoseVariation(String tryOnImageUrl, String poseInstruction) {
        String base64Image = extractBase64FromDataUrl(tryOnImageUrl);
        String mimeType = extractMimeTypeFromDataUrl(tryOnImageUrl);
        
        String prompt = "You are an expert fashion photographer AI. Take this image and regenerate it from a different perspective. The person, clothing, and background style must remain identical. The new perspective should be: \"" + poseInstruction + "\". Return ONLY the final image.";
        
        Map<String, Object> requestBody = buildGeminiRequest(base64Image, mimeType, prompt);
        
        return callGeminiApi(requestBody);
    }
    
    /**
     * Build Gemini API request with single image
     */
    private Map<String, Object> buildGeminiRequest(String base64Image, String mimeType, String prompt) {
        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mime_type", mimeType);
        inlineData.put("data", base64Image);
        
        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("inline_data", inlineData);
        
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Arrays.asList(imagePart, textPart));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Arrays.asList(content));
        
        return requestBody;
    }
    
    /**
     * Build Gemini API request with multiple images
     */
    private Map<String, Object> buildGeminiRequestWithMultipleImages(
        List<Map<String, String>> images, 
        String prompt
    ) {
        List<Map<String, Object>> parts = new ArrayList<>();
        
        // Add all images
        for (Map<String, String> image : images) {
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", image.get("mimeType"));
            inlineData.put("data", image.get("data"));
            
            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inline_data", inlineData);
            parts.add(imagePart);
        }
        
        // Add text prompt
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Arrays.asList(content));
        
        return requestBody;
    }
    
    /**
     * Extract base64 data from data URL
     */
    private String extractBase64FromDataUrl(String dataUrl) {
        if (dataUrl.startsWith("data:")) {
            String[] parts = dataUrl.split(",");
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return dataUrl;
    }
    
    /**
     * Extract MIME type from data URL
     */
    private String extractMimeTypeFromDataUrl(String dataUrl) {
        if (dataUrl.startsWith("data:")) {
            String[] parts = dataUrl.split(";");
            if (parts.length > 0) {
                return parts[0].substring(5); // Remove "data:"
            }
        }
        return "image/png"; // Default
    }
    
    /**
     * Call Gemini API
     */
    private GeminiResponse callGeminiApi(Map<String, Object> requestBody) {
        String endpoint = "/models/" + geminiConfig.getModelName() + ":generateContent?key=" + geminiConfig.getApiKey();
        
        try {
            logger.info("Calling Gemini API: {}", endpoint);
            
            String responseBody = webClient.post()
                .uri(endpoint)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .block();
            
            return parseGeminiResponse(responseBody);
            
        } catch (WebClientResponseException e) {
            logger.error("Gemini API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiApiException("Gemini API returned error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Network error calling Gemini API", e);
            throw new NetworkException("Failed to connect to Gemini API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse Gemini API response
     */
    private GeminiResponse parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            
            // Check for prompt feedback block
            if (root.has("promptFeedback") && root.get("promptFeedback").has("blockReason")) {
                String blockReason = root.get("promptFeedback").get("blockReason").asText();
                String blockMessage = root.get("promptFeedback").has("blockReasonMessage") 
                    ? root.get("promptFeedback").get("blockReasonMessage").asText() 
                    : "";
                throw new GeminiApiException("Request was blocked. Reason: " + blockReason + ". " + blockMessage);
            }
            
            // Find image in candidates
            if (root.has("candidates")) {
                JsonNode candidates = root.get("candidates");
                for (JsonNode candidate : candidates) {
                    if (candidate.has("content") && candidate.get("content").has("parts")) {
                        JsonNode parts = candidate.get("content").get("parts");
                        for (JsonNode part : parts) {
                            // Check for inline_data (REST API format)
                            if (part.has("inline_data") || part.has("inlineData")) {
                                JsonNode inlineData = part.has("inline_data") 
                                    ? part.get("inline_data") 
                                    : part.get("inlineData");
                                    
                                String mimeType = inlineData.has("mime_type")
                                    ? inlineData.get("mime_type").asText()
                                    : inlineData.get("mimeType").asText();
                                String data = inlineData.get("data").asText();
                                
                                String dataUrl = "data:" + mimeType + ";base64," + data;
                                return new GeminiResponse(dataUrl, mimeType);
                            }
                        }
                    }
                }
            }
            
            // Check finish reason
            if (root.has("candidates") && root.get("candidates").size() > 0) {
                JsonNode firstCandidate = root.get("candidates").get(0);
                if (firstCandidate.has("finishReason")) {
                    String finishReason = firstCandidate.get("finishReason").asText();
                    if (!"STOP".equals(finishReason)) {
                        throw new GeminiApiException("Image generation stopped unexpectedly. Reason: " + finishReason);
                    }
                }
            }
            
            throw new GeminiApiException("The AI model did not return an image");
            
        } catch (IOException e) {
            throw new GeminiApiException("Failed to parse Gemini API response", e);
        }
    }
}
