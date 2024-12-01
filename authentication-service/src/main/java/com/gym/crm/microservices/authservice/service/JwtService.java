package com.gym.crm.microservices.authservice.service;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.authservice.entity.JwtBlackToken;
import com.gym.crm.microservices.authservice.entity.Role;
import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.repository.JwtBlackTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    private final JwtBlackTokenRepository blackTokenRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${jwt.access.duration}")
    private Duration duration;

    @Transactional
    public String generateToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + duration.toMillis());

        User user = userService.findByUsername(username);
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("application")
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");

        return objectMapper.convertValue(roles, new TypeReference<>() {
        });
    }

    private Date extractExpiration(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isPresentValidAccessToken(String authorization) {
        return nonNull(authorization) && authorization.startsWith("Bearer ");
    }

    public String extractAccessToken(String authorization) {
        return authorization.substring(7);
    }

    @Transactional
    public void addTokenToBlackList(String token) {
        Date expiration = extractExpiration(token);

        JwtBlackToken blackToken = JwtBlackToken.builder()
                .token(token)
                .expiryDate(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        blackTokenRepository.save(blackToken);
    }

    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return blackTokenRepository.existsByToken(token);
    }

    @Transactional
    @Scheduled(fixedRate = 3_600_000)
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        blackTokenRepository.deleteByExpiryDateBefore(now);
    }
}
