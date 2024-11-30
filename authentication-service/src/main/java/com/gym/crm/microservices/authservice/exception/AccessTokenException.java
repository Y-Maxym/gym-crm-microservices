package com.gym.crm.microservices.authservice.exception;

public class AccessTokenException extends AuthenticationException {

    public AccessTokenException(String message, Integer code) {
        super(message, code);
    }

    public AccessTokenException(String message, Integer code, Throwable cause) {
        super(message, code, cause);
    }
}
