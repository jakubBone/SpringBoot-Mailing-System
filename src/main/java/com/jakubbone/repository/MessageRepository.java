package com.jakubbone.repository;

import com.jakubbone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Counts unread messages for a given recipient
    long countByRecipientIdAndIsReadFalse(String recipientId);
}
