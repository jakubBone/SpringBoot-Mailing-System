package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.integration.common.AbstractIntegrationTest;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class MessageTest extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MessageRepository messageRepository;

    String adminToken;
    String userToken;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
        adminToken = getJwtToken("testadmin", "adminPassword");
        userToken = getJwtToken("testuser", "userPassword");
    }

    @Test
    void shouldReturn201_whenAdminSendsValidMessage() throws Exception {
        SendMessageRequest req = new SendMessageRequest("testuser", "Hello user!");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }
}
