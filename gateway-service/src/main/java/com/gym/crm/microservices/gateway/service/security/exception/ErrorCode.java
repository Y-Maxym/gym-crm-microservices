package com.gym.crm.microservices.gateway.service.security.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNAUTHORIZED_ERROR(40065),
    INVALID_ACCESS_TOKEN(400201);

    ErrorCode(final int code) {
        this.code = code;
    }

    private final Integer code;
}
