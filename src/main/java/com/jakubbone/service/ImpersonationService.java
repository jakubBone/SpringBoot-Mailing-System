package com.jakubbone.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ImpersonationService {
    private final Keycloak keycloakAdminClient;
    private final KeycloakService keycloakService;

    public ImpersonationService(Keycloak keycloakAdminClient, KeycloakService keycloakUserService) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakService = keycloakUserService;
    }

    public String impersonateUser(String targetUsername){
        if (!keycloakService.existsByUsername(targetUsername)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + targetUsername + " not found");
        }

        UsersResource usersResource = keycloakAdminClient.realm(keycloakService.getKeycloakRealm()).users();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(targetUsername, true);

        UserRepresentation targetUserRep = userRepresentations.get(0);

        UserResource userResource = usersResource.get(targetUserRep.getId());
        Map<String, Object> impersonationResponse;
        try {
            impersonationResponse = userResource.impersonate();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to impersonate: Check the Keycloak client configuration");
        }

        String accessToken = (String) impersonationResponse.get("access_token");
        if (accessToken == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to receive impersonaton access token");
        }
        return accessToken;
    }

    public String exitImpersonateUser(){

    }
}
