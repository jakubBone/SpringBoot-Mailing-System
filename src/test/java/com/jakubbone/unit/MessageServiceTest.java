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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    void shouldSendMessage_whenValidRequest() {
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

        String sender = "testuser";
        String recipient = "recipient";
        String content = "hello";

        when(keycloakUserService.existsByUsername(recipient)).thenReturn(true);
        when(messageRepository.countByRecipientIdAndIsReadFalse(recipient)).thenReturn(1L);
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        Message result = messageService.send(sender, recipient, content);

        assertNotNull(result);
        assertEquals(sender, result.getSenderId());
        assertEquals(recipient, result.getRecipientId());
        assertEquals(content, result.getContent());
        assertFalse(result.isRead());
    }

    @Test
    void shouldSendMessage_whenMailboxAlmostFull() {
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

        String sender = "testuser";
        String recipient = "recipient";
        String content = "Hello";

        when(keycloakUserService.existsByUsername(recipient)).thenReturn(true);
        when(messageRepository.countByRecipientIdAndIsReadFalse(recipient)).thenReturn(4L); // 4 z 5
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        Message result = messageService.send(sender, recipient, content);

        assertNotNull(result);
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
    void shouldThrowException_whenRecipientDoesNotExist() {
        String sender = "testuser";
        String recipient = "non-existent";
        String content = "hello";

        when(keycloakUserService.existsByUsername(recipient)).thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.send(sender, recipient, content)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Invalid recipient:" + recipient));
    }

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

        long messageId = 1L;
        String recipient = "recipient";

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.markAsRead(messageId, recipient)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void shouldThrowException_whenMarkingOtherUser() {
        ReflectionTestUtils.setField(messageService, "mailboxLimit", 5);

        long messageId = 1L;
        String recipient = "testuser";
        Message msg = new Message();
        msg.setRecipientId("otheruser");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(msg));



        ResponseStatusException ex = assertThrows(
                        ResponseStatusException.class,
                () -> messageService.markAsRead(messageId, recipient)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void shouldSearchMessages_whenValidPhrase() {
        String username = "testuser";
        String searchPhrase = "hello";
        Pageable pageable = Pageable.unpaged();

        Page<Message> expectedPage = Page.empty();
        when(messageRepository.searchMessages(username, searchPhrase, pageable))
                .thenReturn(expectedPage);

        Page<Message> result = messageService.searchMessages(username, searchPhrase, pageable);

        assertNotNull(result);
        verify(messageRepository).searchMessages(username, searchPhrase, pageable);
    }

    @Test
    void shouldThrowException_whenSearchPhraseEmpty() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.searchMessages("testuser", "", Pageable.unpaged())
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Query too short", ex.getReason());
    }

    @Test
    void shouldThrowException_whenSearchPhraseTooShort() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> messageService.searchMessages("testuser", "a", Pageable.unpaged())
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }


}
