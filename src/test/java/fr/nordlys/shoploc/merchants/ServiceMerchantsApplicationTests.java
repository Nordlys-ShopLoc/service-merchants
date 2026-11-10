package fr.nordlys.shoploc.merchants;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Starts the application against a real PostgreSQL: Flyway migrations and JPA mapping must agree. */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServiceMerchantsApplicationTests {

    @Test
    void context_loads() {
    }
}
