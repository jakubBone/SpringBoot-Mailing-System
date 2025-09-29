package com.jakubbone.integration;

import com.jakubbone.integration.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
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

    // Public endpoints - no authentication required

    @Test
    void shouldReturnActuatorHealth_withoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldReturnApplicationInfo_withoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.app.name").value("SpringBootMailingSystem"))
                .andExpect(jsonPath("$.app.version").value("0.0.1-SNAPSHOT"));
    }

    // Protected endpoints - require ADMIN role

    @Test
    void shouldReturnUptimeMetric_whenAdminAuthenticated() throws Exception {
        String adminToken = getJwtToken("testadmin", "adminPassword");

        mockMvc.perform(get("/actuator/metrics/process.uptime")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$.name").value("process.uptime"))
                .andExpect(jsonPath("$.measurements", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.measurements[0].value", greaterThanOrEqualTo(0.0)));
    }

    @Test
    void shouldReturn403_whenUserTriesToAccessMetrics() throws Exception {
        String userToken = getJwtToken("testuser", "userPassword");

        mockMvc.perform(get("/actuator/metrics/process.uptime")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenAccessingMetricsWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/metrics/process.uptime"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnAvailableEndpoints_whenAdminAuthenticated() throws Exception {
        String adminToken = getJwtToken("testadmin", "adminPassword");

        mockMvc.perform(get("/actuator")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(
                        "application/vnd.spring-boot.actuator.v3+json")))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.health").exists())
                .andExpect(jsonPath("$._links.info").exists())
                .andExpect(jsonPath("$._links.metrics").exists());
    }

    // Shutdown endpoint tests

    @Test
    void shouldReturn403_whenUserTriesToShutdown() throws Exception {
        String userToken = getJwtToken("testuser", "userPassword");

        mockMvc.perform(post("/actuator/shutdown")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenShutdownWithoutAuth() throws Exception {
        mockMvc.perform(post("/actuator/shutdown"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Security tests - verify endpoints are properly protected

    @Test
    void shouldReturn401_whenAccessingRootActuatorWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenUserAccessesRootActuator() throws Exception {
        String userToken = getJwtToken("testuser", "userPassword");

        mockMvc.perform(get("/actuator")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}