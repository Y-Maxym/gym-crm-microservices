package com.gym.crm.app.exception;

import lombok.Getter;

@Getter
public class ServiceUnavailableException extends RuntimeException {

    private final Integer code;

    public ServiceUnavailableException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
