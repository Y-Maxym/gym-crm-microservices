package com.gym.crm.microservices.authservice.exception;

import lombok.Getter;

@Getter
public class PasswordOperationException extends RuntimeException {

    private final Integer code;

    public PasswordOperationException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
