package com.gym.crm.microservices.authservice.service;

import com.auth0.jwt.JWT;
import com.gym.crm.microservices.authservice.entity.JwtBlackToken;
import com.gym.crm.microservices.authservice.exception.AccessTokenException;
import com.gym.crm.microservices.authservice.repository.JwtBlackTokenRepository;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token";

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final JwtBlackTokenRepository blackTokenRepository;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.access.duration}")
    private Duration duration;

    @Transactional
    public String generateToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + duration.toMillis());

        UserDetails user = userDetailsService.loadUserByUsername(username);
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
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
    public void validateToken(String authorization) {
        if (!isPresentValidAccessToken(authorization)) {
            throw new AccessTokenException(INVALID_ACCESS_TOKEN, ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        }

        String token = extractAccessToken(authorization);
        String username = extractUsername(token);

        if (!isValid(token)) {
            throw new AccessTokenException(INVALID_ACCESS_TOKEN, ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        }

        if (shouldAuthenticate(username)) {
            authenticateUserWithToken(username, token);
        }
    }

    private boolean shouldAuthenticate(String username) {
        return nonNull(username) && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Transactional(readOnly = true)
    public void authenticateUserWithToken(String username, String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (isValid(token)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Transactional(readOnly = true)
    public boolean isValid(String token) {
        return !isTokenBlacklisted(token) && !isTokenExpired(token);
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
