package com.jakubbone.version_info_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jakubbone.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldReturnUnauthorizedStatus_whenPasswordIncorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testUsername");
        req.setPassword("xxx");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedStatus_whenUsernameIncorrect(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testUsername");
        req.setUsername("testPassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedStatus_Correct(@Autowired MockMvc mockMvc) throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("java10");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
