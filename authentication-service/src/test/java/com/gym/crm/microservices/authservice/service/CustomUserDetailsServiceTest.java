package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.exception.AuthenticationException;
import com.gym.crm.microservices.authservice.exception.EntityValidationException;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import com.gym.crm.microservices.authservice.util.EntityTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    @DisplayName("Test load user by username functionality")
    void givenUsername_whenLoadUserByUsername_thenReturnUser() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getUsername();

        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        UserDetails actual = service.loadUserByUsername(username);

        // then
        assertThat(actual).isEqualTo(user);
        verify(userService).findByUsername(username);
    }

    @Test
    @DisplayName("Test load user by incorrect username functionality")
    void givenIncorrectUsername_whenLoadUserByUsername_thenReturnUser() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getUsername();

        given(userService.findByUsername(username))
                .willThrow(new EntityValidationException("User not found", 400));

        // when
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> service.loadUserByUsername(username));

        // then
        assertThat(ex.getMessage()).isEqualTo("Invalid username or password");
        assertThat(ex.getCause().getMessage()).isEqualTo("User not found");
        assertThat(ex.getCode()).isEqualTo(ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode());
        verify(userService).findByUsername(username);
    }

    @Test
    @DisplayName("Test retrieve user by username functionality")
    void givenUsername_whenRetrieveUserByUsername_thenReturnUser() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getUsername();

        given(userService.findByUsername(username))
                .willReturn(user);

        // when
        UserDetails actual = ReflectionTestUtils.invokeMethod(service, "retrieveUserByUsername", username);

        // then
        assertThat(actual).isEqualTo(user);
        verify(userService).findByUsername(username);
    }

    @Test
    @DisplayName("Test load retrieve by incorrect username functionality")
    void givenIncorrectUsername_whenRetrieveUserByUsername_thenReturnUser() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();
        String username = user.getUsername();

        given(userService.findByUsername(username))
                .willThrow(new EntityValidationException("User not found", 400));

        // when
        AuthenticationException ex = assertThrows(AuthenticationException.class, () -> {
            ReflectionTestUtils.invokeMethod(service, "retrieveUserByUsername", username);
        });

        // then
        assertThat(ex.getMessage()).isEqualTo("Invalid username or password");
        assertThat(ex.getCause().getMessage()).isEqualTo("User not found");
        assertThat(ex.getCode()).isEqualTo(ErrorCode.INVALID_USERNAME_OR_PASSWORD.getCode());
        verify(userService).findByUsername(username);
    }

}