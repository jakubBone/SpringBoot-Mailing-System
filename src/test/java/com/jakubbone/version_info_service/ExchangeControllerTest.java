package com.jakubbone.version_info_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.version_info_service.controller.domain.model.ExchangeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeControllerTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldReturnExchangedCurrency_whenValidInput() throws Exception {
        //given
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(new BigDecimal("100"));
        request.setFrom("EUR");
        request.setTo("PLN");

        // expected conversion 400.00
        String expectedResponse = "400.00";

        mockMvc.perform(post("/api/currency/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void shouldReturnBedResponse_whenInvalidInput() throws Exception {
        ExchangeRequest request = new ExchangeRequest();
        request.setAmount(new BigDecimal("100"));
        request.setFrom("INVALID");
        request.setTo("PLN");

        mockMvc.perform(post("/api/currency/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }
}
