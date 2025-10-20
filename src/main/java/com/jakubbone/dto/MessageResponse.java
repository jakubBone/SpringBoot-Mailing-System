package com.jakubbone.dto;

import com.jakubbone.model.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Message details response")
public record MessageResponse(
        @Schema(
                description = "Unique message id",
                example = "1",
                minimum = "1"
        )
        Long id,

        @Schema(
                description = "Sender username",
                example = "johndoe"
        )
        String senderUsername,

        @Schema(
                description = "Recipient username",
                example = "johndoe"
        )
        String recipientUsername,

        @Schema(
                description = "Message content",
                example = "Hello user!",
                maxLength = 256
        )
        String content,

        @Schema(
                description = "Date and time of message sending",
                example = "2024-01-15T10:30:45"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "Flag describing whether message is read",
                example = "false"
        )
        boolean isRead
) {
    public static MessageResponse fromEntity(Message msg) {
        return new MessageResponse(
                msg.getId(),
                msg.getSenderId(),
                msg.getRecipientId(),
                msg.getContent(),
                msg.getTimestamp(),
                msg.isRead()
        );
    }
}
