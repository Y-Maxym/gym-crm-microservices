package com.gym.crm.microservices.authservice.exception;

import java.util.List;

public record ValidationError
        (
                int code,
                String message,
                List<FieldError> fields
        ) {

}
