package com.cafefusion.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("test-db")
                    .withUsername("test-user")
                    .withPassword("test-pass");

    @DynamicPropertySource
    private static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);

        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("application.security.jwt.secret-key", () -> "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtYXV0aGVudGljYXRpb24tMTIzNDU2Nzg5MA==");
    }
}