package com.jakubbone.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.User;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {
    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    String adminToken;
    String userToken;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User("testUser", passwordEncoder.encode("testPassword"), "USER");
        User testAdmin = new User("testAdmin", passwordEncoder.encode("testPassword"), "ADMIN");
        userRepository.save(testUser);
        userRepository.save(testAdmin);

        userToken = jwtTokenProvider.createToken(testUser.getUsername(), String.valueOf(testUser.getRole()));
        adminToken = jwtTokenProvider.createToken(testAdmin.getUsername(), String.valueOf(testAdmin.getRole()));
    }

    @Test
    void shouldReturn201_whenValidToken(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "Hello user!");
        mockMvc.perform(post("/api/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sender.username").value("testAdmin"))
                .andExpect(jsonPath("$.recipient.username").value("testUser"))
                .andExpect(jsonPath("$.content").value("Hello user!"));
    }


    @Test
    void shouldReturn401_whenInvalidToken(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "Hello user!");

        mockMvc.perform(post("/api/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    void shouldReturn201_whenInvalidPayload(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "Hello user!");

        mockMvc.perform(post("/api/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    void shouldReturn404_whenRecipientNotFound(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("unknown", "Hello unknown!");

        mockMvc.perform(post("/api/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldReturn404_whenNoContent(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "");

        mockMvc.perform(post("/api/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }
}
