package com.gym.crm.app.exception;

import com.gym.crm.app.rest.exception.FieldError;

import java.util.List;

public class EntityPersistException extends ApplicationException {

    public EntityPersistException(String message, Integer code, List<FieldError> errors) {
        super(message, code, errors);
    }
}
