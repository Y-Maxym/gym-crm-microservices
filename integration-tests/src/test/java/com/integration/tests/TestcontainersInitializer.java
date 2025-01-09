package com.integration.tests;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        DockerComposeContainer<?> environment = new DockerComposeContainer<>(new File("docker-compose.yml"))
                .withOptions("--compatibility")
                .withExposedService("eureka-service", 8761)
                .withExposedService("gateway-service", 8080,
                        Wait.forHttp("/authentication-service/api/v1/login")
                                .withMethod("POST")
                                .forStatusCode(401)
                                .withStartupTimeout(Duration.ofMinutes(10)))
                .withExposedService("gym-crm-service", 8081)
                .withExposedService("trainer-hours-service", 8082)
                .withExposedService("authentication-service", 8083)
                .withExposedService("config-service", 8100)
                .withExposedService("admin-service", 8090)
                .withLocalCompose(true);
        environment.start();

        String gatewayUrl = "http://localhost:" + environment.getServicePort("gateway-service", 8080);
        TestPropertyValues.of("gateway.url=" + gatewayUrl).applyTo(applicationContext.getEnvironment());
    }
}
