package com.gym.crm.microservices.authservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_USERNAME_OR_PASSWORD(4001),
    NULL_ENTITY(4005),
    HASHED_ERROR(40087),
    USER_WITH_USERNAME_NOT_FOUND(400101),
    AUTHENTICATION_ERROR(40061),
    UNAUTHORIZED_ERROR(40065),
    GENERAL_ERROR(4000),
    INVALID_FIELD_FORMAT(400111),
    UNRECOGNIZED_FIELD(400113),
    VALUE_INSTANTIATION(400112),
    INVALID_ACCESS_TOKEN(400201),
    REFRESH_TOKEN_NOT_FOUND(400303),
    INVALID_REFRESH_TOKEN(400301),
    TOO_MANY_FAILED_LOGIN_ATTEMPTS(403002);

    ErrorCode(final int code) {
        this.code = code;
    }

    private final Integer code;
}
