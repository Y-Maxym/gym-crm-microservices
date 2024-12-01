package com.gym.crm.microservices.gateway.service.security.exception;

public record ErrorResponse
        (
                int code,
                String message
        ) {
}
