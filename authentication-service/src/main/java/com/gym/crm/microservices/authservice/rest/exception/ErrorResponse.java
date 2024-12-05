package com.gym.crm.microservices.authservice.rest.exception;

public record ErrorResponse
        (
                int code,
                String message
        ) {
}
