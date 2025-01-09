package com.integration.tests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class TestcontainersConfiguration {

    @Bean
    public WebClient gatewayService(@Value("${gateway.url}") String gatewayUrl) {
        return WebClient.create(gatewayUrl);
    }
}
