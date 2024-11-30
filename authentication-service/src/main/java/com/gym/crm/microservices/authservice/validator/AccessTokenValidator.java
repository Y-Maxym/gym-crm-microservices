package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.exception.AccessTokenException;
import com.gym.crm.microservices.authservice.exception.ErrorCode;
import com.gym.crm.microservices.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenValidator {
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token";

    private final JwtService jwtService;

    public void validate(String authorization) {
        if (!isPresentValidAccessToken(authorization)) {
            throw new AccessTokenException(INVALID_ACCESS_TOKEN, ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        }

        String accessToken = jwtService.extractAccessToken(authorization);
        if (isAccessTokenBlacklisted(accessToken)) {
            throw new AccessTokenException(INVALID_ACCESS_TOKEN, ErrorCode.INVALID_REFRESH_TOKEN.getCode());
        }
    }

    private boolean isPresentValidAccessToken(String authorization) {
        return jwtService.isPresentValidAccessToken(authorization);
    }

    private boolean isAccessTokenBlacklisted(String accessToken) {
        return jwtService.isTokenBlacklisted(accessToken);
    }
}
