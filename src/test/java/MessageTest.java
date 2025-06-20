import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class MessageTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test123");

    @Container
    public static KeycloakContainer<?> keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.2.4")
            .withRealmImportFile("test-mailingsystem-realm.json")
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")

            .withExposedPorts(8080)
            .withEnv("KC_DB", "postgres")
            .withEnv("KC_DB_URL", "jdbc:postgresql://postgres:5432/test_db")
            .withEnv("KC_DB_USERNAME", "test_db")
            .withEnv("KC_DB_PASSWORD", "test123");

    @DynamicPropertySource
    public void registerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getDatabaseName);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("keycloak.base-url", keycloak::getAuthServerUrl);
        registry.add("keycloak.realm", () -> "test-mailingsystem");
        registry.add("keycloak.admin-client-id", () -> "test-mailingsystem");
        registry.add("keycloak.admin-client-secret", () -> "test-secret");
    }
}