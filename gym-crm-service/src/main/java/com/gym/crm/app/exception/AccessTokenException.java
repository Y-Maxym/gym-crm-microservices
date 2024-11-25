package com.gym.crm.app.exception;

public class AccessTokenException extends AuthenticationException {

    public AccessTokenException(String message, Integer code) {
        super(message, code);
    }

    public AccessTokenException(String message, Integer code, Throwable cause) {
        super(message, code, cause);
    }
}
