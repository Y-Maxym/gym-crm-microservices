package com.gym.crm.app.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {

    private final Integer code;

    public AuthenticationException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public AuthenticationException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
