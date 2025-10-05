package com.jakubbone.service;

import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for interacting with Keycloak to manage users
 */
@Service
@Getter
public class KeycloakUserService {
    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloakAdminClient;

    /**
     * Constructs the service with a configured Keycloak admin client
     *
     * @param keycloakAdminClient Keycloak admin client used to perform operations
     */
    public KeycloakUserService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    /**
     * Checks if a user with the given username exists in Keycloak realm.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        List<UserRepresentation> users = keycloakAdminClient.realm(keycloakRealm).users().searchByUsername(username, true);
        return users != null && !users.isEmpty();
    }

    public void assignUserRole(String userId){
        RoleRepresentation role = getRealm()
                .roles()
                .get("USER")
                .toRepresentation();

        getRealm()
            .users()
            .get(userId)
            .roles()
            .realmLevel()
            .add(Collections.singletonList(role));
    }

    public RealmResource getRealm() {
        return keycloakAdminClient.realm(keycloakRealm);
    }
}
