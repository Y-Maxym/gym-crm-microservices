package com.gym.crm.microservices.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loginAttemptService, "maxAttempts", 3);
        ReflectionTestUtils.setField(loginAttemptService, "blockDuration", Duration.ofMinutes(5));
    }

    @Test
    @DisplayName("Test login failed increases attempts")
    void whenLoginFailed_thenAttemptsShouldIncrease() {
        // given
        String username = "testUser";

        // when
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        // then
        assertThat(loginAttemptService.isBlocked(username)).isFalse();
    }

    @Test
    @DisplayName("Test loginFailed blocks user after max attempts")
    void whenLoginFailedMaxAttempts_thenUserShouldBeBlocked() {
        // given
        String username = "testUser";

        // when
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        // then
        assertThat(loginAttemptService.isBlocked(username)).isTrue();
    }

    @Test
    @DisplayName("Test loginSucceeded removes user from attempts")
    void whenLoginSucceeded_thenUserShouldNotBeBlocked() {
        // given
        String username = "testUser";

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        // when
        loginAttemptService.loginSucceeded(username);

        // then
        assertThat(loginAttemptService.isBlocked(username)).isFalse();
    }

    @Test
    @DisplayName("Test isBlocked returns false for non-blocked user")
    void whenIsBlockedCalledOnNonBlockedUser_thenReturnsFalse() {
        // given
        String username = "testUser";

        // when
        boolean actual = loginAttemptService.isBlocked(username);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Test isBlocked after block duration")
    void whenIsBlockedCalledAfterBlockDuration_thenReturnsFalse() throws InterruptedException {
        // given
        String username = "testUser";
        ReflectionTestUtils.setField(loginAttemptService, "blockDuration", Duration.ofSeconds(1));

        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);

        Thread.sleep(1500);

        // when
        boolean actual = loginAttemptService.isBlocked(username);

        // then
        assertThat(actual).isFalse();
    }
}