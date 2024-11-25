package com.gym.crm.app.exception;

public class RefreshTokenException extends AuthenticationException {

    public RefreshTokenException(String message, Integer code) {
        super(message, code);
    }
}
