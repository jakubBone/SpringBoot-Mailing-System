package com.jakubbone.service;

import com.jakubbone.model.Message;
import com.jakubbone.model.User;
import com.jakubbone.repository.MessageRepository;
import com.jakubbone.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Message sendMessage(String fromUsername, String toUsername, String content) {
        User sender = userRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        User recipient = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));

        Message msg = new Message();
        msg.setSenderUsername(sender.getUsername());
        msg.setRecipientUsername(recipient.getUsername());
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());

        return messageRepository.save(msg);
    }
}
