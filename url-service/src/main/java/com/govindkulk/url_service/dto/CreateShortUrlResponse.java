package com.govindkulk.url_service.dto;

public class CreateShortUrlResponse {

    private String shortUrl;
    private String originalUrl;
    private boolean success;
    private String message;

    public CreateShortUrlResponse(String shortUrl, String originalUrl, boolean success, String message) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.success = success;
        this.message = message;
    }

    public CreateShortUrlResponse(){};
    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
    
    private void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    private void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static CreateShortUrlResponse error(
        String message
    ){
        return new CreateShortUrlResponse(null, null, false, message);
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public static CreateShortUrlResponse success(
        String shortUrl,
        String originalUrl,
        String message
    ){
        return new CreateShortUrlResponse(shortUrl, originalUrl, true, message);
    }
    public String toString(){
        return "CreateShortUrlResponse{" +
                "shortUrl='" + shortUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }


}
