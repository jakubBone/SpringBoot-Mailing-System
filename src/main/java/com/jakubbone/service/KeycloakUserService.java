package com.jakubbone.service;

import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for interacting with Keycloak to manage users.
 */
@Service
@Getter
public class KeycloakUserService {
    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloakAdminClient;

    /**
     * Constructs the service with a configured Keycloak admin client.
     *
     * @param keycloakAdminClient Keycloak admin client used to perform operations
     */
    public KeycloakUserService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    /**
     * Checks if a user with the given username exists in the configured realm.
     *
     * @param username username to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        List<UserRepresentation> users = keycloakAdminClient.realm(keycloakRealm).users().search(username);
        return users != null && users.stream().anyMatch(u -> username.equals(u.getUsername()));
    }
}
