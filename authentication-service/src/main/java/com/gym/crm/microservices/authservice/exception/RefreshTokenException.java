package com.gym.crm.microservices.authservice.exception;

public class RefreshTokenException extends AuthenticationException {

    public RefreshTokenException(String message, Integer code) {
        super(message, code);
    }
}
