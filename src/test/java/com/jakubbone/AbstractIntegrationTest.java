package com.jakubbone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.repository.MessageRepository;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // use random available port
@Testcontainers
public class AbstractIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test123");

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("/test-mailingsystem-realm.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin");


    @DynamicPropertySource
    void registerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getDatabaseName);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("keycloak.base-url", keycloak::getAuthServerUrl);
        registry.add("keycloak.realm", () -> "test-mailingsystem");
        registry.add("keycloak.admin-client-id", () -> "test-mailingsystem");
        registry.add("keycloak.admin-client-secret", () -> "test-secret");

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/test
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloak.getAuthServerUrl() + "/realms/t-mailingsystem\");est-mailingsystem/protocol/openid-connect/certs");
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MessageRepository messageRepository;

    String adminToken
    String userToken

    BeforeEach
    void setup(){
        this.adminToken = obtainAccessToken("testAdmin", "admin123")
        this.userToken = obtainAccessToken("testUser", "user123")
    }

    String obtainAccessToken(String username, String password){
        Keycloak keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("test-mailingsystem")
                .clientId("test-mailingsystem")
                .clientSecret("test-secret")
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        return keycloakClient.tokenManager().getAccessToken().getToken();
    }
}