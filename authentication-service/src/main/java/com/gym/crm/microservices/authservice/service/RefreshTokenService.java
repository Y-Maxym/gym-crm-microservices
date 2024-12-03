package com.gym.crm.microservices.authservice.service;

import com.gym.crm.microservices.authservice.entity.RefreshToken;
import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.exception.ErrorCode;
import com.gym.crm.microservices.authservice.exception.RefreshTokenException;
import com.gym.crm.microservices.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found";

    private final RefreshTokenRepository repository;
    private final UserService userService;

    @Value("${jwt.refresh.duration}")
    private Duration duration;

    @Transactional
    public String generateToken(String username) {
        User user = userService.findByUsername(username);
        Long tokenId = repository.findTokenIdByUserUsername(username);

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plus(duration.toMillis(), ChronoUnit.MILLIS))
                .build();

        return repository.save(refreshToken).getToken();
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException(REFRESH_TOKEN_NOT_FOUND, ErrorCode.REFRESH_TOKEN_NOT_FOUND.getCode()));
    }

    @Transactional(readOnly = true)
    public String findUsernameByToken(String token) {
        return repository.findUsernameByToken(token);
    }

    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}
