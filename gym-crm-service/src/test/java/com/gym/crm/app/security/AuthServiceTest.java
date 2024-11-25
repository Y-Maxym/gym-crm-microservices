package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthenticationException;
import com.gym.crm.app.exception.EntityValidationException;
import com.gym.crm.app.rest.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Test authenticate with correct credentials functionality")
    void givenValidCredentials_whenAuthenticate_thenReturnUser() {
        // given
        String username = "username";
        String password = "password";

        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .willReturn(authentication);

        // when
        Authentication result = authService.authenticate(username, password);

        // then
        assertThat(result).isEqualTo(authentication);
    }

    @Test
    @DisplayName("Test authenticate with invalid username or password functionality")
    void givenInvalidUsername_whenAuthenticate_thenThrowAuthenticationException() {
        // given
        String username = "username";
        String password = "password";

        String exceptionMessage = "User with username %s not found".formatted(username);

        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .willThrow(new EntityValidationException(exceptionMessage, ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode()));

        // when
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.authenticate(username, password));

        // then
        assertThat(INVALID_USERNAME_OR_PASSWORD).isEqualTo(exception.getMessage());
        assertThat(exception).hasRootCauseMessage(exceptionMessage);
    }
}