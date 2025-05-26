package com.jakubbone.controller;

import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.Message;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.service.MessageService;
import com.jakubbone.utils.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    public MessageController(MessageService messageService, MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody SendMessageRequest req, Authentication authentication){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String sender = jwt.getToken().getClaim("preferred_username");

        Message savedMessage = messageService.sendMessage(sender, req.getTo(), req.getText());
        return ResponseHandler.success(HttpStatus.CREATED, savedMessage);
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id) {
        Message msg = messageRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        if(!msg.isRead()){
            msg.setRead(true);
        }
        return ResponseEntity.noContent().build();
    }



}
