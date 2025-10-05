package com.jakubbone.service;

import java.util.Collections;

import com.jakubbone.dto.TokenResponse;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    private final KeycloakUserService keycloakUserService;

    public AuthService(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    public void registerUser(String username, String password,
                               String email, String firstName, String lastName) {
        boolean ifUserExists = keycloakUserService.existsByUsername(username);

        if (ifUserExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists: " + username);
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        try (Response response = keycloakUserService.getRealm().users().create(user)) {
            String locationHeader = response.getHeaderString("Location");
            if (locationHeader != null) {
                String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                keycloakUserService.assignUserRole(userId);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
    }

    public TokenResponse loginUser(String username, String password){
        return new TokenResponse(null, null, null);
    }
}
