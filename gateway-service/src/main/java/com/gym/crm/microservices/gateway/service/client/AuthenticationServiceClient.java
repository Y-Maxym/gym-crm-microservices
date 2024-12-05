package com.gym.crm.microservices.gateway.service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationServiceClient {

    private final WebClient.Builder builder;

    public Mono<ResponseEntity<String>> validateToken(String token) {
        return builder.baseUrl("lb://AUTHENTICATION-SERVICE")
                .build()
                .post()
                .uri("/api/v1/validate")
                .headers(httpHeaders -> httpHeaders.add("Authorization", token))
                .exchangeToMono(response ->
                        response.toEntity(String.class));
    }
}
