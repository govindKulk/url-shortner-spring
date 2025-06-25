package com.govindkulk.url_service.service;

import com.govindkulk.url_service.model.UrlMapping;
import com.govindkulk.url_service.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShorteningService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    // Base62 characters for encoding (0-9, A-Z, a-z)
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_URL_LENGTH = 6; // 6 characters for short URL
    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_URL_LENGTH = 8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate a short URL using the recommended base62 approach
     * This is the primary method that should be used
     */
    public String generateShortUrl(String originalUrl, Long userId) {
        // Strategy 1: Try hash-based approach first (most efficient)
        String shortUrl = generateHashBasedShortUrl(originalUrl.concat(userId.toString()));
        
        // If hash-based fails due to collision, try random generation
        if (shortUrl == null) {
            shortUrl = generateRandomShortUrl(SHORT_URL_LENGTH);
        }
        
        return userId.toString().substring(2).concat(shortUrl);
    }

    /**
     * Generate short URL using hash of original URL
     * This ensures same URL always gets same short code (idempotent)
     */
    private String generateHashBasedShortUrl(String originalUrl) {
        try {
            // Create hash of original URL
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(originalUrl.getBytes());
            
            // Convert to base62
            String shortUrl = bytesToBase62(hashBytes, SHORT_URL_LENGTH);
            
            // Check for collision
            if (isShortUrlUnique(shortUrl)) {
                return shortUrl;
            }
            
            // If collision, try with different length
            return bytesToBase62(hashBytes, SHORT_URL_LENGTH + 1);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }

    

    /**
     * Convert byte array to base62 string
     */
    private String bytesToBase62(byte[] bytes, int length) {
        StringBuilder result = new StringBuilder();
        
        // Convert bytes to a large number
        long num = 0;
        for (byte b : bytes) {
            num = (num << 8) | (b & 0xFF);
        }
        
        // Convert to base62
        while (num > 0 && result.length() < length) {
            result.insert(0, BASE62_CHARS.charAt((int) (num % 62)));
            num /= 62;
        }
        
        // Pad with zeros if needed
        while (result.length() < length) {
            result.insert(0, '0');
        }
        
        // Truncate if too long
        if (result.length() > length) {
            return result.substring(0, length);
        }
        
        return result.toString();
    }

    /**
     * Check if short URL is unique in database
     */
    private boolean isShortUrlUnique(String shortUrl) {
        Optional<UrlMapping> existing = urlMappingRepository.findByShortUrl(shortUrl);
        return existing.isEmpty();
    }


    /**
     * Generate random short URL (fallback method)
     */
    private String generateRandomShortUrl(int length) {

        if(length > MAX_URL_LENGTH){
            throw new RuntimeException("Failed to generate short URL after " + MAX_ATTEMPTS + " attempts");
        }
        
        int attempts = 0;
        
        while (attempts < MAX_ATTEMPTS) {
            StringBuilder shortUrl = new StringBuilder();
            
            // Generate random base62 string
            for (int i = 0; i < SHORT_URL_LENGTH; i++) {
                int randomIndex = SECURE_RANDOM.nextInt(BASE62_CHARS.length());
                shortUrl.append(BASE62_CHARS.charAt(randomIndex));
            }
            
            String generatedUrl = shortUrl.toString();
            
            // Check if unique
            if (isShortUrlUnique(generatedUrl)) {
                return generatedUrl;
            }
            
            attempts++;
        }
        
        // If still not unique, increase length
        return generateRandomShortUrl(SHORT_URL_LENGTH + 1);
    }
}
