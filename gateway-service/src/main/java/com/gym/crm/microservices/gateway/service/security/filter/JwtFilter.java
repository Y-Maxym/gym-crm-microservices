package com.gym.crm.microservices.gateway.service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.microservices.gateway.service.security.exception.ErrorCode;
import com.gym.crm.microservices.gateway.service.security.exception.ErrorResponse;
import com.gym.crm.microservices.gateway.service.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token";
    private static final List<String> EXCLUDED_URLS = List.of("authentication-service/api/v1/refresh");

    private final JwtService jwtService;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange,
                             @NonNull WebFilterChain chain) {
        final String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (!isPresentValidToken(authorization) || hasExcludedUrl(exchange)) {
            return chain.filter(exchange);
        }

        try {
            String token = jwtService.extractAccessToken(requireNonNull(authorization));
            String username = jwtService.extractUsername(token);

            if (jwtService.isValid(token, username)) {
                Set<String> roles = jwtService.extractRoles(token);
                exchange = exchange.mutate()
                        .request(r -> r
                                .header("X-Username", username)
                                .header("X-Roles", String.join(",", roles)))
                        .build();

                return chain.filter(exchange);
            }

            return writeUnauthorizedResponse(exchange);
        } catch (Exception e) {
            return writeUnauthorizedResponse(exchange);
        }
    }

    private boolean isPresentValidToken(String authorization) {
        return jwtService.isPresentValidAccessToken(authorization);
    }

    private boolean hasExcludedUrl(ServerWebExchange exchange) {
        String uri = exchange.getRequest().getURI().getPath();

        return EXCLUDED_URLS.stream()
                .anyMatch(uri::startsWith);
    }

    private Mono<Void> writeUnauthorizedResponse(ServerWebExchange exchange) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_ACCESS_TOKEN.getCode(), JwtFilter.INVALID_ACCESS_TOKEN);


        try {
            String json = objectMapper.writeValueAsString(error);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes())));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
