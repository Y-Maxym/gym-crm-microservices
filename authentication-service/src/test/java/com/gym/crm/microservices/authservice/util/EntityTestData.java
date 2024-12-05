package com.gym.crm.microservices.authservice.util;

import com.gym.crm.microservices.authservice.entity.JwtBlackToken;
import com.gym.crm.microservices.authservice.entity.RefreshToken;
import com.gym.crm.microservices.authservice.entity.Role;
import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.model.UserCredentials;

import java.time.LocalDateTime;
import java.util.Set;

public class EntityTestData {

    public static User getPersistedUserJohnDoe() {
        return User.builder()
                .id(1L)
                .username("John.Doe")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getPersistedUserJaneSmith() {
        return User.builder()
                .id(2L)
                .username("Jane.Smith")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getPersistedUserMichaelJohnson() {
        return User.builder()
                .id(3L)
                .username("Michael.Johnson")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getPersistedUserEmilyDavis() {
        return User.builder()
                .id(4L)
                .username("Emily.Davis")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getPersistedUserDavidBrown() {
        return User.builder()
                .id(5L)
                .username("David.Brown")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .roles(Set.of(Role.builder().role("ROLE_ADMIN").build()))
                .build();
    }

    public static User getTransientUserJohnDoe() {
        return User.builder()
                .username("John.Doe")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getTransientUserJaneSmith() {
        return User.builder()
                .username("Jane.Smith")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getTransientUserMichaelJohnson() {
        return User.builder()
                .username("Michael.Johnson")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getTransientUserEmilyDavis() {
        return User.builder()
                .username("Emily.Davis")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static User getTransientUserDavidBrown() {
        return User.builder()
                .username("David.Brown")
                .password("hWncieyCBA6S/1dVgKFm+A==:/esUykUhSBey4glFRvVkJHQtGm1KPe67Pxa3ddZi7EI=")
                .build();
    }

    public static UserCredentials getValidJohnDoeAuthCredentials() {
        return new UserCredentials()
                .username("John.Doe")
                .password("password");
    }

    public static UserCredentials getValidEmilyDavisAuthCredentials() {
        return new UserCredentials()
                .username("Emily.Davis")
                .password("password");
    }

    public static UserCredentials getInvalidJohnDoeAuthCredentials() {
        return new UserCredentials()
                .username("John.Doe")
                .password("incorrect");
    }

    public static UserCredentials getInvalidEmilyDavisAuthCredentials() {
        return new UserCredentials()
                .username("Emily.Davis")
                .password("incorrect");
    }

    public static UserCredentials getNullAuthCredentials() {
        return new UserCredentials()
                .username(null)
                .password(null);
    }

    public static RefreshToken getTransientValidRefreshToken() {
        return RefreshToken.builder()
                .token("token")
                .user(getTransientUserEmilyDavis())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    public static RefreshToken getExpiredRefreshToken() {
        return RefreshToken.builder()
                .token("token")
                .user(getTransientUserEmilyDavis())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().minusDays(7))
                .build();
    }

    public static JwtBlackToken getValidJwtBlackToken() {
        return JwtBlackToken.builder()
                .expiryDate(LocalDateTime.now().plusDays(7))
                .token("token")
                .build();
    }

    public static JwtBlackToken getExpiredJwtBlackToken() {
        return JwtBlackToken.builder()
                .expiryDate(LocalDateTime.now().minusDays(7))
                .token("expired_token")
                .build();
    }
}
