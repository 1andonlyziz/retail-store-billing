package com.retailstore.billing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Integration tests for {@link BillCalculatorApplication}.
 * <p>
 * Uses Testcontainers to spin up real PostgreSQL and MongoDB containers in Docker.
 * Spring Boot's {@link ServiceConnection} automatically wires the container
 * connection details (URL, credentials, port) into the application context so no
 * manual property configuration is needed.
 * </p>
 * <p>
 * <strong>Requires Docker to be running on the host machine.</strong>
 * </p>
 */
@SpringBootTest(properties = {
        "configuration.security.api-key=test-api-key"
})
@Testcontainers
class BillCalculatorApplicationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16")
    );

    @Container
    @ServiceConnection
    static final MongoDBContainer MONGO = new MongoDBContainer(
            DockerImageName.parse("mongo:7.0")
    );

    @Test
    void loadContext() {}

    @Test
    void mainMethodDoesNotThrow() {
        // Pass Testcontainer connection details as args so the fresh Spring Boot context
        // started by main() can connect to the same containers used by this test.
        String[] args = {
                "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "--spring.datasource.username=" + POSTGRES.getUsername(),
                "--spring.datasource.password=" + POSTGRES.getPassword(),
                "--spring.data.mongodb.uri=" + MONGO.getReplicaSetUrl(),
                "--configuration.security.api-key=test-api-key",
                "--server.port=0" // random port to avoid conflicts
        };

        assertDoesNotThrow(() -> BillCalculatorApplication.main(args));
    }
}
