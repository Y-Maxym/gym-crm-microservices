package com.gym.crm.app.security;

import com.auth0.jwt.JWT;
import com.gym.crm.app.entity.JwtBlackToken;
import com.gym.crm.app.repository.JwtBlackTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtBlackTokenRepository blackTokenRepository;

    @Value("${jwt.access.secret-key}")
    private String secretKey;
    private SecretKey key;

    @Value("${jwt.access.duration}")
    private Duration duration;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public String generateToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + duration.toMillis());

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("application")
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Transactional(readOnly = true)
    public Boolean isValid(String token, String username) {
        return !isTokenBlacklisted(token) && !isTokenExpired(token) && extractUsername(token).equals(username);
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
