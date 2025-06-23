package com.govindkulk.user_service.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message){
        super(message);
    }
}
