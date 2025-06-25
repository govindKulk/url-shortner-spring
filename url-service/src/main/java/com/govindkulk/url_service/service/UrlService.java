package com.govindkulk.url_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.govindkulk.url_service.model.UrlMapping;
import com.govindkulk.url_service.repository.UrlMappingRepository;

@Service
public class UrlService {
    
    private final UrlMappingRepository urlMappingRepository;

    private final UrlShorteningService urlShorteningService;
    
    public UrlService(UrlMappingRepository urlMappingRepository, UrlShorteningService urlShorteningService) {
        this.urlMappingRepository = urlMappingRepository;
        this.urlShorteningService = urlShorteningService;
    }

    public UrlMapping createShortUrl(String originalUrl, Long userId){

        String shortUrl = urlShorteningService.generateShortUrl(originalUrl, userId);
        UrlMapping urlMapping = new UrlMapping(originalUrl, shortUrl, userId, LocalDateTime.now(), LocalDateTime.now().plusDays(7), 0);
        urlMappingRepository.save(urlMapping);
        return urlMapping;
    }

    public UrlMapping getOriginalUrl(String shortUrl){
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
    
        
        return urlMapping.get();
    }

    public void updateClickCount(String shortUrl){
        UrlMapping urlMapping = getOriginalUrl(shortUrl);
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
    }


    public List<UrlMapping> getAllUrlsByUserId(Long userId){
        return urlMappingRepository.findByUserId(userId);
    }


  
}
