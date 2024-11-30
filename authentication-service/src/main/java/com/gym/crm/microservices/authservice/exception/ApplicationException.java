package com.gym.crm.microservices.authservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final Integer code;
    private final List<FieldError> errors;

    public ApplicationException(String message, Integer code, List<FieldError> errors) {
        super(message);
        this.code = code;
        this.errors = errors;
    }
}
