package com.jakubbone.integration.common;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {
    // Shared Network in order to avoid MessageTest and ActuatorTest collision
    private static final Network NET = Network.SHARED;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withNetwork(NET)
            .withNetworkAliases("postgres")
            .withUsername("testuser")
            .withPassword("testpass")
            .withDatabaseName("testdb");

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.2.4")
            .withNetwork(NET)
            .withNetworkAliases("keycloak")
            .withRealmImportFile("test-realm.json") // From classpath
            .withEnv("KC_FEATURES", "token-exchange")
            .withEnv("KC_FEATURES", "impersonation")
            .withEnv("KC_DB", "postgres")
            .withEnv("KC_DB_URL_HOST", "postgres")
            .withEnv("KC_DB_URL_DATABASE", postgres.getDatabaseName())
            .withEnv("KC_DB_USERNAME", postgres.getUsername())
            .withEnv("KC_DB_PASSWORD", postgres.getPassword())
            .withEnv("KC_PROXY", "edge")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .dependsOn(postgres)
            .waitingFor(Wait.forHttp("/realms/test")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(2)));
                    // act as healthcheck

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");

        String authServerUrl = keycloak.getAuthServerUrl();
        String issuerUri = authServerUrl + "/realms/test";

        registry.add("keycloak.base-url", () -> authServerUrl);
        registry.add("keycloak.realm", () -> "test");
        registry.add("keycloak.admin-client-id", () -> "test-client-id");
        registry.add("keycloak.admin-client-secret", () -> "1234");

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuerUri);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> issuerUri + "/protocol/openid-connect/certs");
    }

    @BeforeAll
    static void setupKeycloakUsers() {
        Keycloak adminClient = KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .build();

        // Reset password because Keycloak stores it as a hash
        resetUserPassword(adminClient, "a7d68651-6850-4fee-94d0-836c11117754", "adminPassword"); // 'admin'
        resetUserPassword(adminClient, "587244a1-e624-4511-8d8b-e4e851940295", "userPassword"); // 'testuser'
    }

    private static void resetUserPassword(Keycloak adminClient, String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        adminClient.realm("test").users().get(userId).resetPassword(passwordCred);
    }

    protected String getJwtToken(String username, String password) {
        Keycloak keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("test")
                .clientId("test-client-id")
                .clientSecret("1234")
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
        return keycloakClient.tokenManager().getAccessToken().getToken();
    }

}