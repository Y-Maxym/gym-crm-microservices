package com.gym.crm.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.rest.exception.ErrorCode;
import com.gym.crm.app.rest.exception.ErrorResponse;
import com.gym.crm.app.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token";
    private static final String ACCESS_TOKEN_HAS_EXPIRED = "Access token has expired";
    private static final List<String> EXCLUDED_URLS = List.of("/api/v1/refresh");

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        final String authorization = request.getHeader("Authorization");

        if (!isPresentValidToken(authorization) || hasExcludedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtService.extractAccessToken(authorization);
            String username = jwtService.extractUsername(token);

            if (shouldAuthenticate(username)) {
                authenticateUserWithToken(username, token);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            writeUnauthorizedResponse(response, ACCESS_TOKEN_HAS_EXPIRED, ErrorCode.EXPIRED_ACCESS_TOKEN.getCode());
        } catch (Exception e) {
            writeUnauthorizedResponse(response, INVALID_ACCESS_TOKEN, ErrorCode.INVALID_ACCESS_TOKEN.getCode());
        }
    }

    private boolean isPresentValidToken(String authorization) {
        return jwtService.isPresentValidAccessToken(authorization);
    }

    private boolean shouldAuthenticate(String username) {
        return nonNull(username) && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUserWithToken(String username, String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isValid(token, username)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private boolean hasExcludedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return EXCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message, Integer code) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse error = new ErrorResponse(code, message);

        String json = objectMapper.writeValueAsString(error);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
