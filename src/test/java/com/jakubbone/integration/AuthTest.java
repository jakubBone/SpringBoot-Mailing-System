package com.jakubbone.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.dto.LoginRequest;
import com.jakubbone.dto.RegisterRequest;
import com.jakubbone.integration.common.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@AutoConfigureMockMvc
class AuthTest extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    Keycloak keycloakAdminClient;

    @BeforeEach()
    void setup(){
        cleanupTestUsers("testuser");
    }

    private void cleanupTestUsers(String username) {
        try {
            List<UserRepresentation> user = keycloakAdminClient
                    .realm("test")
                    .users()
                    .searchByUsername(username, true);

            keycloakAdminClient.realm("test").users().delete(username);
        } catch (Exception e) {
            // Ignore -> user could not exist
        }
    }

    RegisterRequest createRegisterRequest(String username, String email, String password,
                                                  String firstName, String lastName) {
        return new RegisterRequest(
                username,
                password,
                email,
                firstName,
                lastName
        );
    }

    LoginRequest createLoginRequest(String username, String password){
        return new LoginRequest(
                username,
                password
        );
    }

    @Test
    void shouldReturn201_whenRegisterValidUser() throws Exception {
        RegisterRequest req = createRegisterRequest(
                "newuser",
                "newuser@spring.com",
                "Password123!",
                "new",
                "user"
        );

        // Register user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        // Verify user exists in Keycloak
        List<UserRepresentation> users = keycloakAdminClient
                .realm("test")
                .users()
                .searchByUsername("newuser", true);

        assertEquals("newuser", users.get(0).getUsername());
        assertEquals("newuser@spring.com", users.get(0).getEmail());
    }

    @Test
    void shouldReturn201_whenRegisterExistingUser() throws Exception {
        RegisterRequest req1 = createRegisterRequest(
                "duplicate",
                "first@example.com",
                "Password123!",
                "First",
                "User"
        );

        // First registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req1)))
                .andExpect(status().isCreated());

        // Try to register with same username
        RegisterRequest req2 = createRegisterRequest(
                "duplicate",  // Same username
                "second@example.com",
                "Password123!",
                "Second",
                "User"
        );

        // Second registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req2)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict());

        // Cleanup
        cleanupTestUsers("duplicate");
    }

    @Test
    void shouldReturn200AndAccessToken_whenLoginValidCredentials() throws Exception {
        LoginRequest req = createLoginRequest("testuser", "userPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expireTime").isNumber());
    }

}
