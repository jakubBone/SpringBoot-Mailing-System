package com.jakubbone.controller;

import com.jakubbone.dto.MessageResponse;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.Message;
import com.jakubbone.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest req, Authentication authentication){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String sender = jwt.getToken().getClaim("preferred_username");

        Message savedMsg = messageService.send(sender, req.getTo(), req.getText());
        MessageResponse response = MessageResponse.fromEntity(savedMsg);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   @GetMapping
   public ResponseEntity<Page<MessageResponse>> readMessages(Authentication authentication, Pageable pageable) {
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String recipient = jwt.getToken().getClaim("preferred_username");

        Page<MessageResponse> messages = messageService.readAndMarkAsRead(recipient, pageable).map(MessageResponse::fromEntity);
        return ResponseEntity.ok(messages);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id, Authentication authentication) {
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String recipient = jwt.getToken().getClaim("preferred_username");

        messageService.markAsRead(id, recipient);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MessageResponse>> searchMessages(@RequestParam String phrase, Authentication authentication, Pageable pageable){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String username = jwt.getToken().getClaim("preferred_username");

        Page<MessageResponse> messages = messageService.searchMessages(username, phrase, pageable)
                .map(MessageResponse::fromEntity);
        return ResponseEntity.ok(messages);
    }
}
