package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") // set h2 profile; test uses application-test.properties
@SpringBootTest // @SpringBootTest runs all components with all Spring configuration
@AutoConfigureMockMvc // Without running the server
class LoginControllerTest {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User testUser = new User("testUser", passwordEncoder.encode("testPassword"), "USER");
        userRepository.save(testUser);
    }

    @Test
    void shouldReturn401_whenPasswordIncorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testUser");
        req.setPassword("incorrectPassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenUsernameIncorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("incorrectUsername");
        req.setUsername("testPassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnOk_CredentialsCorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testUser");
        req.setPassword("testPassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnToken_CredentialsCorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testUser");
        req.setPassword("testPassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
