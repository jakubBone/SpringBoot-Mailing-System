package com.jakubbone.repository;

import com.jakubbone.model.Message;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Counts unread messages for a given recipient
    // Lock avoids race condition during mailbox limit check
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    long countByRecipientIdAndIsReadFalse(@Param("recipientId") String recipientId);

    Page<Message> findByRecipientId(String recipientId, Pageable pageable);
}
