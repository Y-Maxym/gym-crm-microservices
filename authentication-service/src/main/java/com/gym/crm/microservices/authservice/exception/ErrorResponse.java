package com.gym.crm.microservices.authservice.exception;

public record ErrorResponse
        (
                int code,
                String message
        ) {
}
