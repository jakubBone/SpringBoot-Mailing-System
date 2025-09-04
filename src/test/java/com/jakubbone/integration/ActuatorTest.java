package com.jakubbone.integration;

import com.jakubbone.integration.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ActuatorTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void shouldReturnActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldReturnApplicationInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.app.name").value("SpringBootMailingSystem"))
                .andExpect(jsonPath("$.app.version").value("0.0.1-SNAPSHOT")); // lub z properties
    }

    @Test
    void shouldReturnUptimeMetric() throws Exception {
        mockMvc.perform(get("/actuator/metrics/process.uptime"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.name").value("process.uptime"))
                .andExpect(jsonPath("$.measurements", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.measurements[0].value", greaterThanOrEqualTo(0.0)));
    }

    @Test
    void shouldReturnAvailableEndpoints() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.health").exists())
                .andExpect(jsonPath("$._links.info").exists())
                .andExpect(jsonPath("$._links.metrics").exists());
    }

    @Test
    void shouldReturnDetailedHealthInfo() throws Exception {
        mockMvc.perform(get("/actuator/health/db"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldShutdown_whenEnabled() throws Exception {
        mockMvc.perform(post("/actuator/shutdown"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("Shutting down")));
    }
}