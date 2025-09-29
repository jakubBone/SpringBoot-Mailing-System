package com.jakubbone.repository;

import com.jakubbone.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByRecipientId(String recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(@Param("recipientId") String recipientId);

    @Query(value = "SELECT * FROM messages WHERE " +
            "(sender_id = :username OR recipient_id = :username) AND " +
            "search_vector @@ to_tsquery('simple', :query) " +
            "ORDER BY ts_rank(search_vector, to_tsquery('simple', :query)) DESC", nativeQuery = true)
    Page<Message> searchMessages(@Param("username") String username, @Param("query") String query, Pageable pageable);


}
