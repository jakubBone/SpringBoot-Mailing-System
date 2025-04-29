package com.jakubbone.service;

public interface MessageService {
    void sendMessage(Long senderId, Long recipientId, String content);
}
