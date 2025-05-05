package com.jakubbone.service;

import com.jakubbone.model.Message;
import com.jakubbone.model.User;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(User fromUsername, User toUsername, String content) {
        User sender = userRepository.findByUsername(fromUsername.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
        User recipient = userRepository.findByUsername(toUsername.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Recipient not found"));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }
}
