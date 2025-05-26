package com.jakubbone.service;

import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
public class KeycloakService {
    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloakAdminClient;

    public KeycloakService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public boolean existsByUsername(String username) {
        List<UserRepresentation> users = keycloakAdminClient.realm(keycloakRealm).users().search(username);
        return users != null && users.stream().anyMatch(u -> username.equals(u.getUsername()));
    }
}
