package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.integration.common.AbstractIntegrationTest;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.repository.MessageRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class MessageTest extends AbstractIntegrationTest {
    String user = "testuser";
    String admin = "testadmin";
    int MAILBOX_LIMIT = 5;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    String adminToken;
    String userToken;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE messages ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE messages ADD COLUMN IF NOT EXISTS search_vector tsvector GENERATED ALWAYS AS (to_tsvector('simple', content)) STORED");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_messages_search_vector ON messages USING GIN (search_vector)");
        adminToken = getJwtToken(admin, "adminPassword");
        userToken = getJwtToken(user, "userPassword");
    }

    private SendMessageRequest createMessageRequest(String recipient, String content) {
        return new SendMessageRequest(recipient, content);
    }

    @Test
    void shouldReturn201_andSentMessage_whenAdminSendsValidMessage() throws Exception {
        SendMessageRequest req = createMessageRequest(user, "Hello testuser!");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn201_andSentMessage_whenUserSendsValidMessage() throws Exception {
        SendMessageRequest req = createMessageRequest(admin, "Hello testadmin!");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn404_whenRecipientNotFound() throws Exception {
        SendMessageRequest req = createMessageRequest("unknown", "Hello user!");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400_whenRecipientUsernameInvalid() throws Exception {
        // Username too short
        SendMessageRequest req = createMessageRequest("ab", "Hello user!");
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest());

        // Username too long
        req = createMessageRequest("verylongusername", "Hello user!");
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest());

        // Username with special characters
        req = createMessageRequest("user123", "Hello user!");
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenMessageTooLong() throws Exception {
        String longMessage = "a".repeat(257); // Exceed the 256 character limit
        SendMessageRequest req = createMessageRequest(user, longMessage);

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenSendingMessageToSelf() throws Exception {
        SendMessageRequest req = createMessageRequest(admin, "Hello myself!");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot send message to yourself"));
    }

    @Test
    void shouldSanitizeHtmlContent_whenSendingMessage() throws Exception {
        String htmlContent = "Hello <script>alert('xss')</script> <b>bold</b> world!";
        SendMessageRequest req = createMessageRequest(user, htmlContent);

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello  <b>bold</b> world!")); // script usunięty
    }

    @Test
    void shouldReturn404_whenNoContent() throws Exception {
        SendMessageRequest req = createMessageRequest(admin, "");

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409_whenMailboxFull() throws Exception {
        SendMessageRequest req = createMessageRequest(user, "Hello testuser!");

        // Send maximum number of messages
        for(int i = 0; i < MAILBOX_LIMIT; i++){
            mockMvc.perform(post("/api/v1/messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(req)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated());
        }

        // Send one more message when mailbox overloaded
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409_whenUnauthorized() throws Exception {
        SendMessageRequest req = createMessageRequest(user, "Hello testuser!");

        for(int i = 0; i < MAILBOX_LIMIT; i++){
            mockMvc.perform(post("/api/v1/messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + "unknown")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(req)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @Transactional
    // The service method countByRecipientIdAndIsReadFalse() call needs the annotation @Transactional
    void shouldReturnTrue_whenMessagesMarkedAsRead() throws Exception {
        SendMessageRequest req = createMessageRequest("testuser", "Hello testuser!");

        // Send by admin
        for(int i = 0; i < 3; i++){
            mockMvc.perform(post("/api/v1/messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(req)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated());
        }

        // Read by user
        for(int i = 1; i <= 3; i++){
            mockMvc.perform(patch("/api/v1/messages/" + i + "/read")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNoContent());
        }

        long unread = messageRepository.countByRecipientIdAndIsReadFalse("testuser");
        Assert.assertEquals(0, unread);
    }

    @Test
    void shouldReturn200_andPageOfMessages_whenRecipientHasMessages() throws Exception {
        SendMessageRequest req = createMessageRequest("testuser", "Hello testuser!");

        // Send by admin
        for(int i = 0; i < 3; i++){
            mockMvc.perform(post("/api/v1/messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsBytes(req)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.numberOfElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].senderUsername").value("testadmin"))
                .andExpect(jsonPath("$.content[0].recipientUsername").value("testuser"))
                .andExpect(jsonPath("$.content[0].content").value("Hello testuser!"));
    }

    @Test
    void shouldReturn200_andEmptyPageOfMessages_whenRecipientNoHasMessages() throws Exception {
        mockMvc.perform(get("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void shouldReturn404_whenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + "unknown"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200_andPageOfMessages_whenSearchingForMessages() throws Exception {
        SendMessageRequest req = createMessageRequest(admin, "Hello testadmin!");

        // Send by user
        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isCreated());

        // Search by user
        mockMvc.perform(get("/api/v1/messages/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .param("phrase", "testadmin"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].content").value("Hello testadmin!"));

        // Search by admin
        mockMvc.perform(get("/api/v1/messages/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("phrase", "testadmin"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldReturn400_whenSearchPhraseIsTooShort() throws Exception {
        mockMvc.perform(get("/api/v1/messages/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .param("phrase", "a"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200_andEmptyPage_whenNoMessagesFound() throws Exception {
        mockMvc.perform(get("/api/v1/messages/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .param("phrase", "nonexistent"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}



