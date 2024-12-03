package com.gym.crm.microservices.authservice.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.authservice.rest.exception.ErrorResponse;
import com.gym.crm.microservices.authservice.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gym.crm.microservices.authservice.rest.exception.ErrorCode.TOO_MANY_FAILED_LOGIN_ATTEMPTS;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class LoginAttemptFilter extends OncePerRequestFilter {
    private static final List<String> INCLUDED_URLS = List.of("/api/v1/login");
    private static final String TOO_MANY_ATTEMPTS_ERROR_MESSAGE = "Account is temporarily blocked due to too many failed login attempts";

    private final LoginAttemptService loginAttemptService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        CustomContentCachingRequestWrapper requestWrapper = new CustomContentCachingRequestWrapper(request);

        if (shouldSkipFilter(requestWrapper, response, filterChain)) {
            return;
        }

        String username = getUsername(requestWrapper);
        if (shouldBlockUser(username, response)) {
            return;
        }

        filterChain.doFilter(requestWrapper, response);

        updateLoginAttemptStatus(response, username);
    }

    private boolean shouldSkipFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!hasIncludedUrl(request)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private boolean shouldBlockUser(String username, HttpServletResponse response) throws IOException {
        if (isNull(username)) {
            return false;
        }
        if (loginAttemptService.isBlocked(username)) {
            writeForbiddenResponse(response);
            return true;
        }
        return false;
    }

    private void updateLoginAttemptStatus(HttpServletResponse response, String username) {
        if (response.getStatus() == HttpServletResponse.SC_OK) {
            loginAttemptService.loginSucceeded(username);
        } else {
            loginAttemptService.loginFailed(username);
        }
    }

    private boolean hasIncludedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return INCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private String getUsername(CustomContentCachingRequestWrapper request) throws IOException {
        byte[] cachedBody = request.getCachedBody();
        Map<String, String> requestBody = new HashMap<>();

        if (cachedBody.length > 0) {
            requestBody = objectMapper.readValue(cachedBody, new TypeReference<>() {
            });
        }

        return requestBody.get("username");
    }

    private void writeForbiddenResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse error = new ErrorResponse(TOO_MANY_FAILED_LOGIN_ATTEMPTS.getCode(), LoginAttemptFilter.TOO_MANY_ATTEMPTS_ERROR_MESSAGE);

        String json = objectMapper.writeValueAsString(error);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
