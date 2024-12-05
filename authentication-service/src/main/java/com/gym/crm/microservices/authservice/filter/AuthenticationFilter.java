package com.gym.crm.microservices.authservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.authservice.rest.exception.ErrorCode;
import com.gym.crm.microservices.authservice.rest.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URLS = List.of("/api/v1/login", "/api/v1/refresh", "/api/v1/validate");
    private static final String INVALID_HEADERS = "Invalid headers";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (hasExcludedUrl(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader("X-Username");
        String roles = request.getHeader("X-Roles");

        if (isNull(username) || isNull(roles)) {
            writeUnauthorizedResponse(response);
            return;
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        User user = new User(username, "", authorities);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean hasExcludedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return EXCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_HEADERS.getCode(), INVALID_HEADERS);

        String json = objectMapper.writeValueAsString(error);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
