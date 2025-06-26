package com.govindkulk.url_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.govindkulk.url_service.model.UrlMapping;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortUrl(String shortUrl);

    List<UrlMapping> findByUserId(Long userId);

    void deleteByShortUrlAndUserId(String shortUrl, Long userId);

    Optional<UrlMapping> findByShortUrlAndUserId(String shortUrl, Long userId);


}
