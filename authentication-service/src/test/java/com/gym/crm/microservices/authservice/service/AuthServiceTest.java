package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.exception.AuthenticationException;
import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import com.gym.crm.microservices.authservice.model.UserCredentials;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import com.gym.crm.microservices.authservice.service.common.BindingResultsService;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;

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

    @Mock
    private BindingResultsService bindingResultsService;

    @InjectMocks
    private AuthService authService;

    private BeanPropertyBindingResult errors;

    @Test
    @DisplayName("Test authenticate with correct credentials functionality")
    void givenValidCredentials_whenAuthenticate_thenReturnUser() {
        // given
        UserCredentials request = EntityTestData.getValidJohnDoeAuthCredentials();
        errors = new BeanPropertyBindingResult(request, "userCredentials");

        String username = request.getUsername();
        String password = request.getPassword();

        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .willReturn(authentication);

        // when
        Authentication result = authService.authenticate(request, errors);

        // then
        assertThat(result).isEqualTo(authentication);
    }

    @Test
    @DisplayName("Test authenticate with invalid username or password functionality")
    void givenInvalidUsername_whenAuthenticate_thenThrowAuthenticationException() {
        // given
        UserCredentials request = EntityTestData.getValidJohnDoeAuthCredentials();
        errors = new BeanPropertyBindingResult(request, "userCredentials");

        String username = request.getUsername();
        String password = request.getPassword();

        String exceptionMessage = "User with username %s not found".formatted(username);

        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .willThrow(new EntityValidationException(exceptionMessage, ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode()));

        // when
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authService.authenticate(request, errors));

        // then
        assertThat(INVALID_USERNAME_OR_PASSWORD).isEqualTo(exception.getMessage());
        assertThat(exception).hasRootCauseMessage(exceptionMessage);
    }
}