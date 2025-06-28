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
    private final KeycloakService keycloakUserService;

    private final PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    public MessageService(MessageRepository messageRepository, KeycloakService keycloakUserService) {
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

        if(messageCount => 5){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send message: Recipient's mailbox is full");
        }

        String sanitizedContent = sanitizer.sanitize(content);

        Message msg = new Message();
        msg.setSenderId(fromUsername);
        msg.setRecipientId(toUsername);
        msg.setContent(sanitizedContent);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }
}
