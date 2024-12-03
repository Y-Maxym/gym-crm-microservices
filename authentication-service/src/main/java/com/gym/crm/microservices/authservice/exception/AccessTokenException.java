package com.gym.crm.microservices.authservice.exception;

public class AccessTokenException extends AuthenticationException {

    public AccessTokenException(String message, Integer code) {
        super(message, code);
    }
}
