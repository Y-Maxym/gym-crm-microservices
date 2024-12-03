package com.gym.crm.microservices.authservice.exception;

import com.gym.crm.microservices.authservice.rest.exception.FieldError;

import java.util.List;

public record ValidationError
        (
                int code,
                String message,
                List<FieldError> fields
        ) {

}
