package com.gym.crm.microservices.gateway.service.security.service;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.gateway.service.security.repository.JwtBlackTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtBlackTokenRepository blackTokenRepository;
    private final ObjectMapper objectMapper;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");

        return objectMapper.convertValue(roles, new TypeReference<>() {
        });
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

    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return blackTokenRepository.existsByToken(token);
    }
}
