package com.gym.crm.microservices.authservice.validator;

import com.gym.crm.microservices.authservice.exception.AccessTokenException;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import com.gym.crm.microservices.authservice.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccessTokenValidatorTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccessTokenValidator accessTokenValidator;

    @Test
    @DisplayName("Test validate with valid token functionality")
    void givenValidToken_whenValidate_thenDoesNotThrowException() {
        // given
        String authorization = "Bearer valid_token";
        String accessToken = "valid_token";

        given(jwtService.isPresentValidAccessToken(authorization))
                .willReturn(true);
        given(jwtService.extractAccessToken(authorization))
                .willReturn(accessToken);
        given(jwtService.isTokenBlacklisted(accessToken))
                .willReturn(false);

        // when & then
        assertDoesNotThrow(() -> accessTokenValidator.isValid(authorization));
    }

    @Test
    @DisplayName("Test validate with invalid access token functionality")
    void givenInvalidAuthorization_whenValidate_thenThrowException() {
        // given
        String authorization = "invalid_authorization_format";

        given(jwtService.isPresentValidAccessToken(authorization)).willReturn(false);

        // when
        boolean actual = accessTokenValidator.isValid(authorization);

        // then
        assertThat(actual).isFalse();
    }


    @Test
    @DisplayName("Test validate with blacklisted access token functionality")
    void givenBlacklistedToken_whenValidate_thenThrowException() {
        // given
        String authorization = "Bearer blacklisted_token";
        String accessToken = "blacklisted_token";

        given(jwtService.isPresentValidAccessToken(authorization)).willReturn(true);
        given(jwtService.extractAccessToken(authorization)).willReturn(accessToken);
        given(jwtService.isTokenBlacklisted(accessToken)).willReturn(true);

        // when & then
        boolean actual = accessTokenValidator.isValid(authorization);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test validate with null authorization header")
    void givenNullAuthorization_whenValidate_thenThrowException() {
        // when & then
        boolean actual = accessTokenValidator.isValid(null);

        // then
        assertThat(actual).isFalse();
    }
}