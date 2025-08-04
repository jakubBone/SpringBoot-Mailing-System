package com.jakubbone.dto;

import com.jakubbone.model.Message;

import java.time.LocalDateTime;

public record MessageResponse(Long id, String senderUsername, String recipientUsername, String content, LocalDateTime timestamp, boolean isRead) {

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
