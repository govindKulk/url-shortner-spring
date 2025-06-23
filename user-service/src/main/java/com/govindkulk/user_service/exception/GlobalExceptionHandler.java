package com.govindkulk.user_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", "Validation Error");
        errorMap.put("success", false);
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errorMap);

    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", "Username does not exist.");
        errorMap.put("success", false);
        
       

        return ResponseEntity.badRequest().body(errorMap);

    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        errorMap.put("success", false);
        
       

        return ResponseEntity.badRequest().body(errorMap);

    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        errorMap.put("success", false);
        
       

        return ResponseEntity.status(403).body(errorMap);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleException(RuntimeException ex){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", "An error occurred");
        errorMap.put("success", false);

        logger.error("An error occurred: {}", ex.getMessage());

        return ResponseEntity.internalServerError().body(errorMap);
       
    }


    
}
