package com.gym.crm.app.rest.exception;

import java.util.List;

public record ValidationError
        (
                int code,
                String message,
                List<FieldError> fields
        ) {

}
