package com.jakubbone.service;

import java.util.Collections;
import java.util.Map;

import com.jakubbone.dto.TokenResponse;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client-secret}")
    private String adminClientSecret;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value(("${keycloak.realm}"))
    private String keycloakRealm;

    private final KeycloakUserService keycloakUserService;
    private final RestTemplate restTemplate;

    public AuthService(KeycloakUserService keycloakUserService, RestTemplate restTemplate) {
        this.keycloakUserService = keycloakUserService;
        this.restTemplate = restTemplate;
    }

    public void registerUser(String username, String password,
                               String email, String firstName, String lastName) {

        boolean ifUserExists = keycloakUserService.existsByUsername(username);
        if (ifUserExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists: " + username);
        }

        UserRepresentation user = createUserRepresentation(username, password, email,
                                                            firstName, lastName);

        try (Response response = keycloakUserService.getRealm().users().create(user)) {
            handleKeycloakResponse(response);
        } catch (ResponseStatusException e){
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private UserRepresentation createUserRepresentation(String username, String password, String email,
                                                        String firstName, String lastName ){
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
        return user;
    }

    private void handleKeycloakResponse(Response response) {
        // Handle SUCCESS (2xx)
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            // Keycloak returns the created user ID in the Location header
            // Extract the UUID at the end to assign the USER role to the newly created user
            String locationHeader = response.getHeaderString("Location");
            if (locationHeader == null) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Registration successful but Location header was missing"
                );
            }
            String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
            keycloakUserService.assignUserRole(userId);
            return;
        }

        // Handle error
        String errorBody = response.hasEntity()
                ? response.readEntity(String.class)
                : "No error details";

        // Handle CONFLICT (409)
        // Parse the error message to provide user-friendly feedback
        if (response.getStatus() == 409) {
            String errorLower = errorBody.toLowerCase();
            if (errorLower.contains("email")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
            }
            if (errorLower.contains("username")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this username already exists");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict: " + errorBody);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid registration data: " + errorBody);
    }

    public TokenResponse loginUser(String username, String password){
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", adminClientId);
        params.add("client_secret", adminClientSecret);
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String accessToken = (String) response.getBody().get("access_token");
                int expires_in = (int) response.getBody().get("expires_in");
                String tokenType = (String) response.getBody().get("tokenType");

                return new TokenResponse(accessToken, expires_in, tokenType);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login failed: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Authentication service unavailable");
        }
    }
}
