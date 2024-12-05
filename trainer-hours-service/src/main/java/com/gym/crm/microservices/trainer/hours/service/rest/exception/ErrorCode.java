package com.gym.crm.microservices.trainer.hours.service.rest.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_HEADERS(403003);

    ErrorCode(final int code) {
        this.code = code;
    }

    private final Integer code;
}
