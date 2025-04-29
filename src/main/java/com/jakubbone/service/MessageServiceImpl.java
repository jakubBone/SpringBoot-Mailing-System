package com.jakubbone.service;

import com.jakubbone.model.User;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.repository.UserRepository;

public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessage(User sender, User recipient, String content) {
    }
}
