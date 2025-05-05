package com.jakubbone.controller;

import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.Message;
import com.jakubbone.service.MessageService;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(@Valid @RequestBody SendMessageRequest req, Authentication authentication){
        String senderUsername = authentication.name();
        Message savedMessage = messageService.sendMessage(senderUsername, req.getTo(), req.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

}
