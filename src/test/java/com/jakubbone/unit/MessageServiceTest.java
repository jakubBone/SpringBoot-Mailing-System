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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    MessageRepository messageRepository;

    @Mock
    KeycloakUserService keycloakUserService;

    @InjectMocks
    MessageService messageService;

    @Test
    void shouldThrowException_whenSendingToSelf() {
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

    @Test
    void shouldThrowException_whenMailboxFull() {
        String sender = "testuser";
        String recipient = "recipient";
        String content = "Hello";

        when(keycloakUserService.existsByUsername(recipient)).thenReturn(true);
        when(messageRepository.countByRecipientIdAndIsReadFalse(recipient)).thenReturn(5L);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.send(sender, recipient, content)
        );

        assertEquals("Cannot send message: Recipient's mailbox is full", ex.getReason());
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}
