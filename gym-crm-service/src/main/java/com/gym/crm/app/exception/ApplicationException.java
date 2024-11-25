package com.gym.crm.app.exception;

import com.gym.crm.app.rest.exception.FieldError;
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
