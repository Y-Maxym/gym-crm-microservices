package com.integration.tests;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Testcontainers
public class IntegrationTestBase {

    protected static WebClient gatewayService;

    private static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("docker-compose.yml"))
                    .withOptions("--compatibility")
                    .withExposedService("eureka-service", 8761)
                    .withExposedService("gateway-service", 8080,
                            Wait.forHttp("/authentication-service/api/v1/login")
                                    .withMethod("POST")
                                    .forStatusCode(401)
                                    .withStartupTimeout(Duration.ofMinutes(10)))
                    .withExposedService("gym-crm-app", 8081)
                    .withExposedService("trainer-hours-service", 8082)
                    .withExposedService("authentication-service", 8083)
                    .withExposedService("config-service", 8100)
                    .withExposedService("admin-service", 8090)
                    .withLocalCompose(true);

    @BeforeAll
    public static void setUp() {
        environment.start();

        gatewayService = WebClient.create("http://localhost:" + environment.getServicePort("gateway-service", 8080));
    }
}
