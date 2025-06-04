package com.jakubbone.dto;

import com.jakubbone.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    public static MessageResponse fromEntity(Message msg) {
        return new MessageResponse(
                msg.getId(),
                msg.getSenderUsername(),
                msg.getRecipientUsername(),
                msg.getContent(),
                msg.getTimestamp(),
                msg.isRead()
        );
    }
}
