package com.jakubbone.service;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.jakubbone.model.Message;
import com.jakubbone.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final KeycloakUserService keycloakUserService;
    private final int MAILBOX_LIMIT = 5;

    private final PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public MessageService(MessageRepository messageRepository, KeycloakUserService keycloakUserService) {
        this.messageRepository = messageRepository;
        this.keycloakUserService = keycloakUserService;
    }

    @Transactional
    public Message sendMessage(String fromUsername, String toUsername, String content) {
        if(toUsername.equals(fromUsername)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send message to yourself");
        }

        if (!keycloakUserService.existsByUsername(toUsername)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient " + toUsername + " not found");
        }

        long messageCount = messageRepository.countByRecipientIdAndIsReadFalse(toUsername);

        if(messageCount >= MAILBOX_LIMIT){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send message: Recipient's mailbox is full");
        }

        //HTML sanitization policy to protect against XSS attacks
        String sanitizedContent = sanitizer.sanitize(content);

        Message msg = new Message();
        msg.setSenderId(fromUsername);
        msg.setRecipientId(toUsername);
        msg.setContent(sanitizedContent);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    @Transactional
    public void markMessageAsRead(Long messageId) {
        Message msg = messageRepository.findById(messageId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if(!msg.isRead()){
            msg.setRead(true);
            messageRepository.save(msg);
        }
    }
}
