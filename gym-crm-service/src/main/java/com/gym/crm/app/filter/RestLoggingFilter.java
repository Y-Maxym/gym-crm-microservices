package com.gym.crm.app.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.app.logging.MessageHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.gym.crm.app.util.Constants.INFO_REST_LOGGING_FILTER_REQUEST;
import static com.gym.crm.app.util.Constants.INFO_REST_LOGGING_FILTER_RESPONSE;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_URLS = List.of("/swagger-ui", "/v1/api-docs", "/actuator/prometheus");

    private final ObjectMapper objectMapper;
    private final MessageHelper messageHelper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        CustomContentCachingRequestWrapper requestWrapper = new CustomContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        if (hasExcludedUrl(request)) {
            filterChain.doFilter(requestWrapper, responseWrapper);
            return;
        }

        String transactionId = MDC.get("transactionId");

        logRequestDetails(requestWrapper, transactionId);

        filterChain.doFilter(requestWrapper, responseWrapper);

        logResponseDetails(responseWrapper, transactionId);
        responseWrapper.copyBodyToResponse();
    }

    private void logRequestDetails(CustomContentCachingRequestWrapper request, String transactionId) throws IOException {
        byte[] cachedBody = request.getCachedBody();
        String requestBody = new String(cachedBody, StandardCharsets.UTF_8);

        if (cachedBody.length > 0) {
            Object json = objectMapper.readValue(cachedBody, Object.class);
            requestBody = objectMapper.writeValueAsString(json);
            requestBody = hideCredentials(requestBody);
        }

        log.info(messageHelper.getMessage(INFO_REST_LOGGING_FILTER_REQUEST,
                request.getMethod(),
                request.getRequestURI(),
                requestBody,
                transactionId));
    }

    private void logResponseDetails(ContentCachingResponseWrapper response, String transactionId) throws IOException {
        byte[] cachedBody = response.getContentAsByteArray();
        String responseBody = new String(cachedBody, StandardCharsets.UTF_8);

        if (cachedBody.length > 0) {
            Object json = objectMapper.readValue(cachedBody, Object.class);
            responseBody = objectMapper.writeValueAsString(json);
            responseBody = hideCredentials(responseBody);
        }

        log.info(messageHelper.getMessage(INFO_REST_LOGGING_FILTER_RESPONSE,
                response.getStatus(),
                responseBody,
                transactionId));
    }

    private boolean hasExcludedUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return EXCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private boolean hasCredentials(String body) {
        return body.contains("username") || body.contains("password");
    }

    private String hideCredentials(String body) {
        if (hasCredentials(body)) {
            return body.replaceAll("\"username\":\"[^\"]*\"", "\"username\":\"****\"")
                    .replaceAll("\"password\":\"[^\"]*\"", "\"password\":\"****\"");
        }

        return body;
    }
}
