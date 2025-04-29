package com.jakubbone.service;

import com.jakubbone.model.User;

public interface MessageService {
    void sendMessage(User sender, User recipient, String content);
}
