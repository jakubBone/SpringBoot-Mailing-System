package com.jakubbone.repository;

import com.jakubbone.model.Message;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByRecipientId(String recipientId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    long countByRecipientIdAndIsReadFalse(@Param("recipientId") String recipientId);

    @Query(value = "SELECT * FROM messages WHERE " +
            "search_vector @@ plainto_tsquery('simple', :query" +
            "ORDER BY timestamp DESC", nativeQuery = true)
    Page<Message> searchMessages(@Param("query") String query, Pageable pageable);


}
