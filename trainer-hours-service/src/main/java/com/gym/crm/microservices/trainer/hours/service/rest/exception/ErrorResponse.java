package com.gym.crm.microservices.trainer.hours.service.rest.exception;

public record ErrorResponse
        (
                int code,
                String message
        ) {
}
