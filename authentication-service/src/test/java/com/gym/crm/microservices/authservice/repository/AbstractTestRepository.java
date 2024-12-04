package com.gym.crm.microservices.authservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public abstract class AbstractTestRepository<T> {

    protected static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

    @Autowired
    protected T repository;

    @PersistenceContext
    protected EntityManager entityManager;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withUsername("gcatest")
                .withPassword("gcapass")
                .withDatabaseName("gym-crm-test");

        POSTGRE_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @BeforeEach
    public void beforeEach() {
        entityManager.createQuery("DELETE FROM User").executeUpdate();
    }
}
