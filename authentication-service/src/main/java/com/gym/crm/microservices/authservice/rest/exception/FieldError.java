package com.gym.crm.microservices.authservice.rest.exception;

public record FieldError
        (
                String field,
                String message
        ) {
    public FieldError(org.springframework.validation.FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }
}