package com.gym.crm.app.facade.validator;

import com.gym.crm.app.entity.RefreshToken;
import com.gym.crm.app.exception.RefreshTokenException;
import com.gym.crm.app.rest.exception.ErrorCode;
import com.gym.crm.app.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {
    private static final String INVALID_REFRESH_TOKEN = "Invalid Refresh Token";
    private final RefreshTokenService service;

    public void validate(String token) {
        if (isNull(token) || !isValid(token)) {
            throw new RefreshTokenException(INVALID_REFRESH_TOKEN, ErrorCode.INVALID_REFRESH_TOKEN.getCode());
        }
    }

    private boolean isValid(String token) {
        RefreshToken refreshToken = service.findByToken(token);

        return ofNullable(refreshToken)
                .map(refToken -> refreshToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}
