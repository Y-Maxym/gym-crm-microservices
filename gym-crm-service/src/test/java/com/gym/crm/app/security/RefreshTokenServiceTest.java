package com.gym.crm.app.security;

import com.gym.crm.app.entity.RefreshToken;
import com.gym.crm.app.entity.User;
import com.gym.crm.app.exception.RefreshTokenException;
import com.gym.crm.app.repository.RefreshTokenRepository;
import com.gym.crm.app.rest.exception.ErrorCode;
import com.gym.crm.app.service.UserService;
import com.gym.crm.app.utils.EntityTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "duration", Duration.ofHours(1));
    }

    @Test
    @DisplayName("Test generate token by valid username functionality")
    void givenValidUsername_whenGenerateToken_thenGenerateToken() {
        // given
        User user = EntityTestData.getPersistedUserJohnDoe();

        given(userService.findByUsername(user.getUsername()))
                .willReturn(user);
        given(repository.findTokenIdByUserUsername(user.getUsername()))
                .willReturn(1L);
        given(repository.save(any(RefreshToken.class)))
                .willAnswer((Answer<RefreshToken>) invocation -> {
                    RefreshToken refreshToken = invocation.getArgument(0);
                    refreshToken.setToken(UUID.randomUUID().toString());
                    return refreshToken;
                });

        // when
        String actual = refreshTokenService.generateToken(user.getUsername());

        // then
        assertThat(actual).isNotNull();

        verify(userService).findByUsername(user.getUsername());
        verify(repository).findTokenIdByUserUsername(user.getUsername());
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Test find by valid token functionality")
    void givenValidToken_whenFindByToken_thenFindByToken() {
        // given
        RefreshToken refreshToken = EntityTestData.getTransientValidRefreshToken();

        given(repository.findByToken(refreshToken.getToken()))
                .willReturn(Optional.of(refreshToken));

        // when
        RefreshToken actual = refreshTokenService.findByToken(refreshToken.getToken());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(refreshToken);

        verify(repository).findByToken(refreshToken.getToken());
    }

    @Test
    @DisplayName("Test find by invalid token functionality")
    void givenInvalidToken_whenFindByToken_thenFindByToken() {
        // given
        RefreshToken refreshToken = EntityTestData.getTransientValidRefreshToken();

        given(repository.findByToken(refreshToken.getToken()))
                .willReturn(Optional.empty());

        // when
        RefreshTokenException ex = assertThrows(RefreshTokenException.class, () -> {
            refreshTokenService.findByToken(refreshToken.getToken());
        });

        // then
        assertThat(ex.getMessage()).isEqualTo("Refresh token not found");
        assertThat(ex.getCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode());

        verify(repository).findByToken(refreshToken.getToken());
    }

    @Test
    @DisplayName("Test find username by token functionality")
    void givenValidToken_whenFindByUsernameByToken_thenUsernameFound() {
        // given
        RefreshToken refreshToken = EntityTestData.getTransientValidRefreshToken();
        String username = EntityTestData.getPersistedUserJohnDoe().getUsername();

        given(repository.findUsernameByToken(refreshToken.getToken()))
                .willReturn(username);

        // when
        String actual = refreshTokenService.findUsernameByToken(refreshToken.getToken());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(username);

        verify(repository).findUsernameByToken(refreshToken.getToken());
    }

    @Test
    @DisplayName("Test delete by token functionality")
    void givenToken_whenDeleteByToken_thenRepositoryDeleteCalled() {
        // given
        String token = "valid_token";

        // when
        refreshTokenService.deleteByToken(token);

        // then
        verify(repository).deleteByToken(token);
    }

}