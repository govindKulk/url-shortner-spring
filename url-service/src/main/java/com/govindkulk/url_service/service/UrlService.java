package com.govindkulk.url_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govindkulk.url_service.dto.CreateShortUrlResponse;
import com.govindkulk.url_service.exception.UrlNotFoundException;
import com.govindkulk.url_service.model.UrlMapping;
import com.govindkulk.url_service.repository.UrlMappingRepository;



@Service
@Transactional
public class UrlService {
    
    private final UrlMappingRepository urlMappingRepository;

    private final UrlShorteningService urlShorteningService;
    
    public UrlService(UrlMappingRepository urlMappingRepository, UrlShorteningService urlShorteningService) {
        this.urlMappingRepository = urlMappingRepository;
        this.urlShorteningService = urlShorteningService;
    }

    public CreateShortUrlResponse createShortUrl(String originalUrl, Long userId){

        String shortUrl = urlShorteningService.generateShortUrl(originalUrl, userId);

        if(urlMappingRepository.findByShortUrl(shortUrl).isPresent()){
            CreateShortUrlResponse.error("Short url already exists");
        }

        System.out.println("shortUrl: from service " + shortUrl);
        UrlMapping urlMapping = new UrlMapping(originalUrl, shortUrl, userId, LocalDateTime.now(), LocalDateTime.now().plusDays(7), 0);
        urlMappingRepository.save(urlMapping);
        return CreateShortUrlResponse.success(shortUrl, originalUrl, "Short url created successfully");
    }

    public UrlMapping getOriginalUrlByShortUrlAndUserId(String shortUrl, Long userId){
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrlAndUserId(shortUrl, userId);
        
        if(urlMapping.isEmpty()){
            throw new UrlNotFoundException("Url not found for user " + userId);
        }
        
        return urlMapping.get();
    }

    public UrlMapping getOriginalUrlByShortUrl(String shortUrl){
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping.isEmpty()){
            throw new UrlNotFoundException("Url not found");
        }
        return urlMapping.get();
    }

    public void updateClickCount(String shortUrl){
        UrlMapping urlMapping = getOriginalUrlByShortUrl(shortUrl);
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
    }


    public List<UrlMapping> getAllUrlsByUserId(Long userId){
        return urlMappingRepository.findByUserId(userId);
    }

    public void deleteUrl(String shortUrl, Long userId){

        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrlAndUserId(shortUrl, userId);

        if(urlMapping.isEmpty()){
            throw new UrlNotFoundException("Url not found");
        }   
        urlMappingRepository.deleteByShortUrlAndUserId(shortUrl, userId);
    }




  
}
