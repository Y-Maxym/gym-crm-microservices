package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.entity.Role;
import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.repository.JwtBlackTokenRepository;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private final Duration duration = Duration.ofHours(1);

    @Mock
    private JwtBlackTokenRepository blackTokenRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtService jwtService;

    @Test
    @DisplayName("Test generate token functionality")
    void givenUsername_whenGenerateToken_thenValidTokenGenerated() {
        // given
        User user = EntityTestData.getPersistedUserDavidBrown();
        String username = user.getUsername();
        ReflectionTestUtils.setField(jwtService, "duration", duration);

        given(userDetailsService.loadUserByUsername(username))
                .willReturn(EntityTestData.getPersistedUserDavidBrown());

        // when
        String actual = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUsername(actual);

        // then
        assertThat(actual).isNotNull();
        assertThat(extractedUsername).isEqualTo(username);
        assertThat(jwtService.isValid(actual)).isTrue();
    }

    @Test
    @DisplayName("Test extract username functionality")
    void givenUsername_extractUsername_thenCorrectUsernameShouldBeReturned() {
        // given
        User user = EntityTestData.getPersistedUserDavidBrown();
        String username = user.getUsername();
        ReflectionTestUtils.setField(jwtService, "duration", duration);

        given(userDetailsService.loadUserByUsername(username))
                .willReturn(EntityTestData.getPersistedUserDavidBrown());

        String token = jwtService.generateToken(username);

        // when
        String actual = jwtService.extractUsername(token);

        // then
        assertThat(actual).isEqualTo(username);
    }

    @Test
    @DisplayName("Test expired token functionality")
    void givenExpiredToken_whenIsValid_thenTokenIsExpired() throws Exception {
        // given
        User user = EntityTestData.getPersistedUserDavidBrown();
        String username = user.getUsername();
        ReflectionTestUtils.setField(jwtService, "duration", Duration.ofMillis(1));

        given(blackTokenRepository.existsByToken(any()))
                .willReturn(false);
        given(userDetailsService.loadUserByUsername(username))
                .willReturn(EntityTestData.getPersistedUserDavidBrown());

        // when & then
        String token = jwtService.generateToken(username);
        Thread.sleep(100);
        Boolean actual = jwtService.isValid(token);

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test isPresentValidAccessToken functionality with valid token")
    void givenValidAuthorizationHeader_whenIsPresentValidAccessToken_thenReturnsTrue() {
        // given
        String authorization = "Bearer validToken";

        // when
        boolean result = jwtService.isPresentValidAccessToken(authorization);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test isPresentValidAccessToken functionality with invalid token")
    void givenInvalidAuthorizationHeader_whenIsPresentValidAccessToken_thenReturnsFalse() {
        // given
        String authorization = "InvalidToken";

        // when
        boolean result = jwtService.isPresentValidAccessToken(authorization);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Test extractAccessToken functionality with valid token")
    void givenAuthorizationHeader_whenExtractAccessToken_thenReturnsToken() {
        // given
        String authorization = "Bearer validToken";

        // when
        String token = jwtService.extractAccessToken(authorization);

        // then
        assertThat(token).isEqualTo("validToken");
    }

    @Test
    @DisplayName("Test isTokenBlacklisted functionality with blacklisted token")
    void givenBlacklistedToken_whenIsTokenBlacklisted_thenReturnsTrue() {
        // given
        String token = "blacklistedToken";
        given(blackTokenRepository.existsByToken(token)).willReturn(true);

        // when
        boolean isBlacklisted = jwtService.isTokenBlacklisted(token);

        // then
        assertThat(isBlacklisted).isTrue();
    }

    @Test
    @DisplayName("Test isTokenBlacklisted functionality with non-blacklisted token")
    void givenNonBlacklistedToken_whenIsTokenBlacklisted_thenReturnsFalse() {
        // given
        String token = "nonBlacklistedToken";
        given(blackTokenRepository.existsByToken(token)).willReturn(false);

        // when
        boolean isBlacklisted = jwtService.isTokenBlacklisted(token);

        // then
        assertThat(isBlacklisted).isFalse();
    }

    @Test
    @DisplayName("Test removing expired tokens functionality")
    void whenRemoveExpiredTokens_thenDeleteExpiredTokensCalled() {
        // when
        jwtService.removeExpiredTokens();

        // then
        verify(blackTokenRepository).deleteByExpiryDateBefore(any());
    }
}