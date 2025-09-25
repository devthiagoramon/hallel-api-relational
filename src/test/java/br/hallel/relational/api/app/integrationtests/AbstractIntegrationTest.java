package br.hallel.relational.api.app.integrationtests;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new  PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("hallel-db-test")
            .withUsername("postgres")
            .withPassword("testpassword");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void setDatasourceProprieties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
