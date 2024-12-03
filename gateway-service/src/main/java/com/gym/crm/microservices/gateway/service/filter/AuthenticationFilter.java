package com.gym.crm.microservices.gateway.service.filter;

import com.gym.crm.microservices.gateway.service.client.AuthenticationServiceClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Object> {

    private static final List<String> EXCLUDED_URLS = List.of(
            "/authentication-service/api/v1/login",
            "/authentication-service/api/v1/refresh",
            "/authentication-service/api/v1/validate");

    private final AuthenticationServiceClient client;

    public AuthenticationFilter(@Lazy AuthenticationServiceClient client) {
        this.client = client;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String requestPath = request.getURI().getPath();
            if (isExcludedPath(requestPath)) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey("Authorization")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = request.getHeaders().getFirst("Authorization") == null ? "sdf" : request.getHeaders().getFirst("Authorization");

            return client.validateToken(token)
                    .flatMap(response -> {
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-Username", response.getHeaders().getFirst("X-Username"))
                                .header("X-Roles", response.getHeaders().getFirst("X-Roles"))
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    });
        };
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_URLS.stream().anyMatch(path::startsWith);
    }
}
