package com.jakubbone.repository;

import com.jakubbone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

// JPARepository provides the ready methods
// e.g. save(), findById(), findAll(), delete()
// for Message Entity with the key type Long
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Spring Data JPA has a built-in parser that analyzes method names according to a set pattern
    long countByRecipientIdAndIsReadFalse(String recipientId);
}
