package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.entity.RefreshToken;
import com.gym.crm.microservices.authservice.service.RefreshTokenService;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RefreshTokenValidatorTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private RefreshTokenValidator refreshTokenValidator;

    @Test
    @DisplayName("Test validate by valid token functionality")
    void givenValidToken_whenValidate_thenDoesNotThrowException() {
        // given
        RefreshToken refreshToken = EntityTestData.getTransientValidRefreshToken();
        String token = refreshToken.getToken();

        given(refreshTokenService.findByToken(token))
                .willReturn(refreshToken);

        // when & then
        assertDoesNotThrow(() -> refreshTokenValidator.isValid(token));
    }

    @Test
    @DisplayName("Test validate by null token functionality")
    void givenNullToken_whenValidate_thenThrowException() {
        // when
        boolean actual = refreshTokenValidator.isValid(null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test validate by invalid token functionality")
    void givenInvalidToken_whenValidate_thenThrowException() {
        // given
        String token = "invalid_token";

        given(refreshTokenService.findByToken(token))
                .willReturn(null);

        // when
        boolean actual = refreshTokenValidator.isValid(token);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test validate by expired token functionality")
    void givenExpiredToken_whenValidate_thenThrowException() {
        // given
        String token = "expired_token";
        RefreshToken expiredRefreshToken = EntityTestData.getExpiredRefreshToken();

        given(refreshTokenService.findByToken(token))
                .willReturn(expiredRefreshToken);

        // when
        boolean actual = refreshTokenValidator.isValid(token);

        // then
        assertThat(actual).isFalse();
    }
}