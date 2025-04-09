package com.jakubbone.version_info_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.domain.model.ExchangeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeControllerTest {
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldReturnOkStatus_whenValidInput(@Autowired MockMvc mockMvc) throws Exception {
        ExchangeRequest req = new ExchangeRequest();
        req.setAmount(new BigDecimal("100.00"));
        req.setFrom("EUR");
        req.setTo("PLN");

        mockMvc.perform(post("/api/currency/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadResponseStatus_whenMissingField(@Autowired MockMvc mockMvc) throws Exception {
        // Request with missing 'from' field
        ExchangeRequest req = new ExchangeRequest();
        req.setAmount(new BigDecimal("100.00"));
        req.setTo("PLN");

        mockMvc.perform(post("/api/currency/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnExchangedValue_whenValidInput(@Autowired MockMvc mockMvc) throws Exception {
        ExchangeRequest req = new ExchangeRequest();
        req.setAmount(new BigDecimal("100.00"));
        req.setFrom("EUR");
        req.setTo("PLN");

        // Expected conversion 400.00
        mockMvc.perform(post("/api/currency/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("400.0"));
    }

    @Test
    void shouldReturnInvalidRequest_whenMissingField(@Autowired MockMvc mockMvc) throws Exception {
        // Request with missing 'from' field
        ExchangeRequest req = new ExchangeRequest();
        req.setAmount(new BigDecimal("100"));
        req.setTo("PLN");

        mockMvc.perform(post("/api/currency/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid request: amount/from/to must are required"));
    }

    @Test
    void shouldReturnError_whenUnsupportedCurrency(@Autowired MockMvc mockMvc) throws Exception {
        // Request with unsupported currency
        ExchangeRequest req = new ExchangeRequest();
        req.setAmount(new BigDecimal("100"));
        req.setFrom("NON-EXISTENT");
        req.setTo("PLN");

        mockMvc.perform(post("/api/currency/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Unsupported currency"));
    }
}