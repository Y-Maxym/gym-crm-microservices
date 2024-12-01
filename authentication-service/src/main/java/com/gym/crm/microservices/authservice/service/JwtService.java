package com.gym.crm.microservices.authservice.service;

import com.auth0.jwt.JWT;
import com.gym.crm.microservices.authservice.entity.JwtBlackToken;
import com.gym.crm.microservices.authservice.entity.Role;
import com.gym.crm.microservices.authservice.entity.User;
import com.gym.crm.microservices.authservice.repository.JwtBlackTokenRepository;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    private final JwtBlackTokenRepository blackTokenRepository;
    private final UserService userService;

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

    private Date extractExpiration(String token) {
        return JWT.decode(token).getExpiresAt();
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
