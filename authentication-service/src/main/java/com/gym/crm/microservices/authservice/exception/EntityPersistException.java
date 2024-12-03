package com.gym.crm.microservices.authservice.exception;

import com.gym.crm.microservices.authservice.rest.exception.FieldError;

import java.util.List;

public class EntityPersistException extends ApplicationException {

    public EntityPersistException(String message, Integer code, List<FieldError> errors) {
        super(message, code, errors);
    }
}
