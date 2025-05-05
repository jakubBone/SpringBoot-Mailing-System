package com.jakubbone.service;

import com.jakubbone.model.Message;

public interface MessageService {
    Message sendMessage(String senderUsername, String recipientUsername, String content);
}
