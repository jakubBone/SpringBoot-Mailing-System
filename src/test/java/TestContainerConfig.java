import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class TestContainerConfig {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("spring_db")
            .withUsername("spring_user")
            .withPassword("spring123");

    @Container
    public static KeycloakContainer<?> keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.2.4")
            .withRealmImportFile("mailingsystem-realm.json")
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("keycloak.base-url", () -> keycloak.getAuthServerUrl());
        registry.add("keycloak.realm", () -> "mailingsystem"); /
        registry.add("keycloak.admin-client-id", () -> "admin-cli");
        registry.add("keycloak.admin-client-secret", () -> "YOUR_ADMIN_CLIENT_SECRET");
        registry.add("keycloak.resource", () -> "account");
        registry.add("keycloak.auth-server-url", () -> keycloak.getAuthServerUrl());
        registry.add("keycloak.ssl-required", () -> "none");
        registry.add("keycloak.public-client", () -> "true");
    }
}
