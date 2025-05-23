package com.jakubbone.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakUserService {
    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public boolean existsByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(username);
        return users != null && users.stream().anyMatch(u -> username.equals(u.getUsername()));
    }
}
