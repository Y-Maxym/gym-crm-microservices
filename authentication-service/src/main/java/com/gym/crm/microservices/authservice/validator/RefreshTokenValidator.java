package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.entity.RefreshToken;
import com.gym.crm.microservices.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {
    private final RefreshTokenService service;

    public boolean isValid(String token) {
        RefreshToken refreshToken = service.findByToken(token);

        return isNotBlank(token) && ofNullable(refreshToken)
                .map(refToken -> refreshToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}
