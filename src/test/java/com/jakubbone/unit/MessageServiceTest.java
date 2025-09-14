package com.jakubbone.unit;

import com.jakubbone.repository.MessageRepository;
import com.jakubbone.service.KeycloakUserService;
import com.jakubbone.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    MessageRepository messageRepository;

    @Mock
    KeycloakUserService keycloakUserService;

    @InjectMocks
    MessageService messageService;

    @Test
    void shouldThrowException_whenSSendingToSelf() {
        String sender = "testuser";
        String recipient = "testuser";
        String content = "Hello";

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.send(sender, recipient, content)
        );

        assertEquals("Cannot send message to yourself", ex.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
