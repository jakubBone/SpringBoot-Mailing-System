package com.jakubbone.controller;

import com.jakubbone.dto.ErrorResponse;
import com.jakubbone.dto.MessageResponse;
import com.jakubbone.dto.SendMessageRequest;
import com.jakubbone.model.Message;
import com.jakubbone.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag( name = "Messages", description = "Send, read and search message operations")
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sends a message to another user.
     *
     * @param req the message request containing recipient and content
     * @param authentication the authenticated user sending the message
     * @return the created message with HTTP 201 status
     * @throws ResponseStatusException if recipient not found (404) or mailbox full (409)
     */
    @Operation(
            summary = "Send message",
            description = "Sends messages to another user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Message sent successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Recipient not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Users mailbox is full",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest req, Authentication authentication){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String sender = jwt.getToken().getClaim("preferred_username");

        Message savedMsg = messageService.send(sender, req.getTo(), req.getText());
        MessageResponse response = MessageResponse.fromEntity(savedMsg);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves paginated messages for the authenticated user and marks them as read.
     *
     * @param authentication the authenticated user
     * @param pageable pagination parameters (page, size, sort)
     * @return page of messages with HTTP 200 status
     */

    @Operation(
            summary = "Read messages",
            description = "Get message from logged user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Return page of messages",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
   @GetMapping
   public ResponseEntity<Page<MessageResponse>> readMessages(Authentication authentication, Pageable pageable) {
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String recipient = jwt.getToken().getClaim("preferred_username");

        Page<MessageResponse> messages = messageService.readAndMarkAsRead(recipient, pageable).map(MessageResponse::fromEntity);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marks a specific message as read.
     *
     * @param id the message ID
     * @param authentication the authenticated user (must be the recipient)
     * @return HTTP 204 No Content on success
     * @throws ResponseStatusException if message not found (404) or access denied (403)
     */

    @Operation(
            summary = "Mark message as read",
            description = "Marks a specific message as read",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Message read"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Message not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id, Authentication authentication) {
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String recipient = jwt.getToken().getClaim("preferred_username");

        messageService.markAsRead(id, recipient);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches messages by phrase using PostgreSQL full-text search.
     *
     * @param phrase the search query (minimum 2 characters)
     * @param authentication the authenticated user
     * @param pageable pagination parameters
     * @return page of matching messages with HTTP 200 status
     * @throws ResponseStatusException if phrase too short (400)
     */

    @Operation(
            summary = "Search messages",
            description = "Searches messages by phrase",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Message found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Phrase too short",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<Page<MessageResponse>> searchMessages(@RequestParam String phrase, Authentication authentication, Pageable pageable){
        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        String username = jwt.getToken().getClaim("preferred_username");

        Page<MessageResponse> messages = messageService.searchMessages(username, phrase, pageable)
                .map(MessageResponse::fromEntity);
        return ResponseEntity.ok(messages);
    }
}
