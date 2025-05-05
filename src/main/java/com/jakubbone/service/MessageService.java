package com.jakubbone.service;

import com.jakubbone.model.Message;
import com.jakubbone.model.User;

public interface MessageService {
    Message sendMessage(String senderUsername, String recipientUsername, String content);
}
