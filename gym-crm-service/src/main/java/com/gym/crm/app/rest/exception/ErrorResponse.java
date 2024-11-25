package com.gym.crm.app.rest.exception;

public record ErrorResponse
        (
                int code,
                String message
        ) {
}
