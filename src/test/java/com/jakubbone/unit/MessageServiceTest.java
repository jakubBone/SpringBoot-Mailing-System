package com.jakubbone.unit;

import com.jakubbone.model.Message;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.service.KeycloakUserService;
import com.jakubbone.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
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
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

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

    @Test
    void shouldSanitizeContent_whenSendingMessage() {
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

        String sender = "testuser";
        String recipient = "recipient";
        String content = "Hello <script>alert('XSS')</script> <b>world</b>";

        when(keycloakUserService.existsByUsername(recipient)).thenReturn(true);
        when(messageRepository.countByRecipientIdAndIsReadFalse(recipient)).thenReturn(1L);
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        Message msg = messageService.send(sender, recipient, content);

        assertFalse(msg.getContent().contains("<script>"));
        assertTrue(msg.getContent().contains("<b>world</b>"));
    }

    @Test
    void shouldThrowException_whenMarkingNotExistentMessage() {
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

        long messageId = 10L;
        String recipient = "recipient";

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());


        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.markAsRead(messageId, recipient)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
