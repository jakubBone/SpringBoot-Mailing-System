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
    public Message sendMessage(String sender, String recipient, String content) {
        if(recipient.equals(sender)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send message to yourself");
        }

        if (!keycloakUserService.existsByUsername(recipient)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid recipient:" + recipient);
        }

        long messageCount = messageRepository.countByRecipientIdAndIsReadFalse(recipient);

        if(messageCount >= MAILBOX_LIMIT){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send message: Recipient's mailbox is full");
        }

        //HTML sanitization policy to protect against XSS attacks
        String sanitizedContent = sanitizer.sanitize(content);

        Message msg = new Message();
        msg.setSenderId(sender);
        msg.setRecipientId(recipient);
        msg.setContent(sanitizedContent);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    @Transactional
    public void markMessageAsRead(Long id, String recipientId) {
        Message msg = messageRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource unavailable"));

        if(!msg.getRecipientId().equals(recipientId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this message");
        }

        if(!msg.isRead()){
            msg.setRead(true);
            messageRepository.save(msg);
        }
    }
}
