package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import com.jakubbone.utils.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ImpersonationControllerTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    String adminToken;
    String impersonatedAdminToken;

    String userToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User testUser = new User("testUser", "USER", "GITHUB");
        User testAdmin = new User("testAdmin", "ADMIN", "LOCAL");
        userRepository.save(testUser);
        userRepository.save(testAdmin);
        adminToken = jwtTokenProvider.createToken("testAdmin", "ADMIN");
        impersonatedAdminToken = jwtTokenProvider.createImpersonationToken("testUser");
        userToken = jwtTokenProvider.createToken("testUser", "USER");
    }

    // LOGIN IMPERSONATION TESTS

    @Test
    void shouldReturnOk_ImpersonationLogin_WhenAuthorized(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("targetUsername", "testUser"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403__ImpersonationLogin_WhenNotAdmin(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("targetUsername", "adminUser"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403_ImpersonationLogin_WhenTargetUserNotFound(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("targetUsername", "anotherUser"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn500_ImpersonationLogin_WhenNoParams(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                // without targetUser param
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturn500_ImpersonationLogin_WhenNoToken(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("targetUsername", "testUser"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    // LOGOUT IMPERSONATION TESTS

    @Test
    void shouldReturnOk_ImpersonationLogout_WhenAuthorized(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/logout/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + impersonatedAdminToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }


    @Test
    void shouldReturn500_ImpersonationLogout_WhenInvalidToken(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturn500_ImpersonationLogout_WhenNoToken(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/admin/v1/login/impersonation")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }
}
