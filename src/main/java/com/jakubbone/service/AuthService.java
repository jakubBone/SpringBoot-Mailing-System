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
            // Check if status 2xx (SUCCESSFUL)
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                // Success -> looking for header Location
                String locationHeader = response.getHeaderString("Location");
                if (locationHeader != null) {
                    String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                    keycloakUserService.assignUserRole(userId);
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration successful but Location header was missing.");
                }
            } else {
                // ERROR - Keycloak return status 4xx lub 5xx
                // Read error details from Keycloak
                String errorBody = response.hasEntity() ? response.readEntity(String.class) : "No error details";

                // Default set as CONFLICT
                HttpStatus springStatus = HttpStatus.CONFLICT;
                String errorMessage = "Registration failed";

                // Find reason error basis on response
                if (response.getStatus() == 409) {
                    if (errorBody.toLowerCase().contains("email")) {
                        errorMessage = "User with this email already exists";
                    } else if (errorBody.toLowerCase().contains("username")) {
                        errorMessage = "User with this username already exists";
                    } else {
                        errorMessage = "Conflict: " + errorBody;
                    }
                } else {
                    springStatus = HttpStatus.BAD_REQUEST;
                    errorMessage = "Invalid registration data: " + errorBody;
                }
                throw new ResponseStatusException(springStatus, errorMessage);
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed: " + e.getMessage());
        }
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
