package com.integration.tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class IntegrationTests extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test Authentication and Retrieve JWT Token")
    void givenValidCredentials_whenLoginIsRequested_thenJwtTokenIsRetrieved() throws Exception {
        // given
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "password");

        String authUrl = "/authentication-service/api/v1/login";
        String requestBody = objectMapper.writeValueAsString(credentials);

        // when
        Mono<String> tokenResponse = gatewayService.post()
                .uri(authUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(response -> Mono.justOrEmpty(response.headers().asHttpHeaders().getFirst("Authorization")));

        String jwtToken = tokenResponse.block();

        // then
        assertNotNull(jwtToken, "JWT Token is null");
    }
}
