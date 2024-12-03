package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenValidator {
    private final JwtService jwtService;

    public boolean isValid(String authorization) {
        if (!isPresentValidAccessToken(authorization)) {
            return false;
        }

        String accessToken = jwtService.extractAccessToken(authorization);
        return !isAccessTokenBlacklisted(accessToken);
    }

    private boolean isPresentValidAccessToken(String authorization) {
        return jwtService.isPresentValidAccessToken(authorization);
    }

    private boolean isAccessTokenBlacklisted(String accessToken) {
        return jwtService.isTokenBlacklisted(accessToken);
    }
}
