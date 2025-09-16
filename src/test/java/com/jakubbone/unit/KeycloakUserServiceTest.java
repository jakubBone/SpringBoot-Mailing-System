package com.jakubbone.unit;

import com.jakubbone.service.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeycloakUserServiceTest {
    @Mock
    Keycloak keycloakAdminClient;

    @InjectMocks
    KeycloakUserService keycloakUserService;

    @Mock
    RealmResource realmResource;

    @Mock
    UsersResource usersResource;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(keycloakUserService, "keycloakRealm", "testRealm");
    }

    @Test
    void shouldReturnTrue_WhenUserExists(){
        String username = "testuser";
        List<UserRepresentation> users = Arrays.asList(new UserRepresentation());


        when(keycloakAdminClient.realm("testRealm")).thenReturn(realmResource);
        when(keycloakAdminClient.realm("testRealm").users()).thenReturn(usersResource);
        when(keycloakAdminClient.realm("testRealm").users().searchByUsername(username,true)).thenReturn(users);

        boolean exists = keycloakUserService.existsByUsername(username);

        assertTrue(exists);
    }

    @Test
    void shouldReturnTrue_WhenUserDoesNotExist(){
        String username = "nonexistent";
        List<UserRepresentation> users = Collections.emptyList();

        when(keycloakAdminClient.realm("testRealm")).thenReturn(realmResource);
        when(keycloakAdminClient.realm("testRealm").users()).thenReturn(usersResource);
        when(keycloakAdminClient.realm("testRealm").users().searchByUsername(username,true)).thenReturn(users);

        boolean exists = keycloakUserService.existsByUsername(username);

        assertFalse(exists);
    }
}
