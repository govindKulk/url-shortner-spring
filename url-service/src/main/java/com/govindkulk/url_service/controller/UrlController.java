package com.govindkulk.url_service.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govindkulk.url_service.model.UrlMapping;
import com.govindkulk.url_service.service.UrlService;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello World");
    }
    
    @PostMapping
    public ResponseEntity<UrlMapping> createShortUrl(
            @RequestBody CreateUrlRequest request,
            @RequestHeader(value = "X-User-ID", required = false) String userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        
        Long userIdLong = Long.parseLong(userId);

        UrlMapping urlMapping = null;
        try{

             urlMapping = urlService.createShortUrl(request.getOriginalUrl(), userIdLong);
        }catch(Exception e){
            System.out.println("Error creating short url: " + e.getMessage());
            return ResponseEntity.status(400).body(null);
        }
        return ResponseEntity.ok(urlMapping);
    }

    @GetMapping
    public ResponseEntity<List<UrlMapping>> getUserUrls(
            @RequestHeader(value = "X-User-ID", required = false) String userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        
        Long userIdLong = Long.parseLong(userId);
        List<UrlMapping> urls = urlService.getAllUrlsByUserId(userIdLong);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirect(@PathVariable String shortUrl) {
        // This endpoint is for redirects and doesn't need authentication
        UrlMapping urlMapping = urlService.getOriginalUrl(shortUrl);
        urlService.updateClickCount(shortUrl);
        
        return ResponseEntity.status(302)
                .location(URI.create(urlMapping.getOriginalUrl()))
                .build();
    }

    @GetMapping("/stats/{shortUrl}")
    public ResponseEntity<UrlMapping> getStats(@PathVariable String shortUrl){
        UrlMapping urlMapping = urlService.getOriginalUrl(shortUrl);
        return ResponseEntity.ok(urlMapping);
    }
}

class CreateUrlRequest {
    private String originalUrl;
    
    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
}
