package com.govindkulk.url_service.exception;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler  {
    
    // Optional.get() throws NoSuchElementException if the element is not present
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex){
        
        return ResponseEntity.status(404).body("URL not found");
    }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<String> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex){
        return ResponseEntity.status(500).body("Internal server error");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex){
        return ResponseEntity.status(400).body("Invalid user id");
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUrlNotFoundException(UrlNotFoundException ex){
        return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
    }
}
