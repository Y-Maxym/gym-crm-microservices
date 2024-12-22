package com.gym.crm.microservices.trainer.hours.service.repository;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class MongoDbTestContainerConfiguration {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
}
