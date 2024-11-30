package com.gym.crm.microservices.gateway.service.fallback;

public record ErrorEntity
        (
                Integer code,
                String message
        ) {
}
