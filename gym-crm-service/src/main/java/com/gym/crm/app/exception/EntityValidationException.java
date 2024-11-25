package com.gym.crm.app.exception;

import lombok.Getter;

@Getter
public class EntityValidationException extends RuntimeException {

    private final Integer code;

    public EntityValidationException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
