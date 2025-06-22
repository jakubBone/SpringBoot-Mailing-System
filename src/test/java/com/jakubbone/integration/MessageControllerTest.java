package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    ObjectMapper mapper;

    @Autowired
    MessageRepository messageRepository;

    String adminToken;
    String userToken;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
    }

    @Test
    void shouldReturn201_whenAdminValidToken(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "Hello user!");
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.sender.username").value("testAdmin"))
                .andExpect(jsonPath("$.data.recipient.username").value("testUser"))
                .andExpect(jsonPath("$.data.content").value("Hello user!"));
    }


    @Test
    void shouldReturn401_whenUserValidToken(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testAdmin", "Hello admin!");
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.sender.username").value("testUser"))
                .andExpect(jsonPath("$.data.recipient.username").value("testAdmin"))
                .andExpect(jsonPath("$.data.content").value("Hello admin!"));

    }

    @Test
    void shouldReturn404_whenRecipientNotFound(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("unknown", "Hello unknown!");

        mockMvc.perform(post("/api/v1/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_whenNoContent(@Autowired MockMvc mockMvc) throws Exception {
        SendMessageRequest req = new SendMessageRequest("testUser", "");

        mockMvc.perform(post("/api/v1/messages")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }
}
