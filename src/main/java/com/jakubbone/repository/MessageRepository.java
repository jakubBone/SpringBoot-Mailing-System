package com.jakubbone.repository;

import com.jakubbone.model.Message;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Counts unread messages for a given recipient
    @Lock(LockModeType.PESSIMISTIC_READ)
    long countByRecipientIdAndIsReadFalse(String recipientId);

    Page<Message> findByRecipientId(String recipientId, Pageable pageable);
}
