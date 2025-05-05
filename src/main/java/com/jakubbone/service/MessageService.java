package com.jakubbone.service;

import com.jakubbone.model.Message;
import com.jakubbone.model.User;

public interface MessageService {
    Message sendMessage(User sender, User recipient, String content);
}
