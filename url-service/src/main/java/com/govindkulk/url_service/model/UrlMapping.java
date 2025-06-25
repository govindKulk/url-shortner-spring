package com.govindkulk.url_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "original_url", nullable = false)
    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    @Column(name = "short_url", unique = true, nullable = false)
    private String shortUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);

    @Column(name = "click_count")
    private int clickCount = 0;

    public UrlMapping() {};

    public UrlMapping(String originalUrl, String shortUrl, Long userId, LocalDateTime createdAt, LocalDateTime expirationDate, int clickCount) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
        this.clickCount = clickCount;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }   

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UrlMapping{" +
                "id='" + id + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", createdAt=" + createdAt +
                ", expirationDate=" + expirationDate +
                ", clickCount=" + clickCount +
                '}';
    }
}
