package com.jakubbone.repository;

import com.jakubbone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for Message entity.
 * Provides CRUD methods e.g. save(), findById(), findAll(), delete(), countBy()
 * and custom query methods derived from method names.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * Counts unread messages for a given recipient.
     * @param recipientId recipient username
     * @return count of unread messages
     */
    long countByRecipientIdAndIsReadFalse(String recipientId);
}
