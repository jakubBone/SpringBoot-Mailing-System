package com.jakubbone.controller;

import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.Message;
import com.jakubbone.service.MessageService;
import com.jakubbone.utils.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody SendMessageRequest req, Authentication authentication){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String sender = jwt.getToken().getClaim("preferred_username");

        Message savedMessage = messageService.sendMessage(sender, req.getTo(), req.getText());
        return ResponseHandler.success(HttpStatus.CREATED, savedMessage);
    }
}
