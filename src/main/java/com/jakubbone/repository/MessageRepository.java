package com.jakubbone.repository;

import com.jakubbone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

// JPARepository provides the ready methods
// e.g. save(), findById(), findAll(), delete(), countBy()
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Spring Data JPA has a built-in parser that analyzes method names
    long countByRecipientIdAndIsReadFalse(String recipientId);
}
