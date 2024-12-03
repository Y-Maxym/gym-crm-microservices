package com.gym.crm.microservices.authservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.authservice.rest.exception.ErrorResponse;
import com.gym.crm.microservices.authservice.validator.AccessTokenValidator;
import com.gym.crm.microservices.authservice.validator.RefreshTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private static final List<String> INCLUDED_URLS = List.of("/api/v1/refresh");

    private static final String REFRESH_TOKEN_ERROR_MESSAGE = "Invalid refresh token";
    private static final String ACCESS_TOKEN_ERROR_MESSAGE = "Invalid access token";

    private static final String COOKIE_NAME = "refreshToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final RefreshTokenValidator refreshTokenValidator;
    private final AccessTokenValidator accessTokenValidator;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (shouldSkipFilter(request, response, filterChain)) {
            return;
        }

        String refreshToken = getCookie(request);
        if (!refreshTokenValidator.isValid(refreshToken)) {
            ErrorResponse error = new ErrorResponse(INVALID_REFRESH_TOKEN.getCode(), REFRESH_TOKEN_ERROR_MESSAGE);
            writeForbiddenResponse(response, error);
            return;
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (!accessTokenValidator.isValid(authorization)) {
            ErrorResponse error = new ErrorResponse(INVALID_ACCESS_TOKEN.getCode(), ACCESS_TOKEN_ERROR_MESSAGE);
            writeForbiddenResponse(response, error);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws IOException, ServletException {
        if (!hasIncludedUrl(request)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private boolean hasIncludedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return INCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private String getCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void writeForbiddenResponse(HttpServletResponse response, ErrorResponse error) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(error);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}