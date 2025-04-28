package com.jakubbone.version_info_service.unit;

import com.jakubbone.controller.LoginController;
import com.jakubbone.dto.LoginRequest;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @InjectMocks
    LoginController loginController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldReturnToken_whenLoginSuccess_ReturnsToken() {
        LoginRequest req = new LoginRequest("alice", "password123");
        User user = new User("alice", "testHashedPassword", "USER");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.createToken("alice", "USER")).thenReturn("fake-jwt-token");

        ResponseEntity <?> resp = loginController.login(req);
        Map respBody = (Map) resp.getBody();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("fake-jwt-token", respBody.get("token"));
    }

    @Test
    void shouldReturn401_whenPasswordIncorrect() {
        LoginRequest req = new LoginRequest("alice", "incorrectPassword");
        User user = new User("alice", "testHashedPassword", "USER");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrectPassword", user.getPasswordHash())).thenReturn(false);

        ResponseEntity <?> resp = loginController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void shouldReturn401_whenUserIncorrect() {
        LoginRequest req = new LoginRequest("incorrectUser", "password123");
        User user = new User("incorrectUser", "password123", "USER");

        when(userRepository.findByUsername("incorrectUser")).thenReturn(Optional.empty());

        ResponseEntity <?> resp = loginController.login(req);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }
}
