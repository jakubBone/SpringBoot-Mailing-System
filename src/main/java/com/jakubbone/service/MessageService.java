package com.jakubbone.service;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.jakubbone.model.Message;
import com.jakubbone.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    @Value("${mailbox.limit}")
    private int mailboxLimit;

    private final MessageRepository messageRepository;
    private final KeycloakUserService keycloakUserService;
    private final PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    // Synchronization object to prevent race conditions when checking mailbox limits
    private final Object mailboxLock = new Object();

    public MessageService(MessageRepository messageRepository, KeycloakUserService keycloakUserService) {
        this.messageRepository = messageRepository;
        this.keycloakUserService = keycloakUserService;
    }

    /**
     * Sends a message from one user to another with validation and sanitization.
     * Checks mailbox capacity before sending.
     *
     * @param sender the username of the sender
     * @param recipient the username of the recipient
     * @param content the message content (will be HTML sanitized)
     * @return the saved message entity
     * @throws ResponseStatusException if sending to self (400), recipient not found (404),
     *                                 or mailbox full (409)
     */
    @Transactional
    public Message send(String sender, String recipient, String content) {
        if(recipient.equals(sender)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send message to yourself");
        }

        if (!keycloakUserService.existsByUsername(recipient)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid recipient:" + recipient);
        }

        // Synchronizacja całej operacji sprawdzania limitu i zapisu
        synchronized (mailboxLock) {
            long messageCount = messageRepository.countByRecipientIdAndIsReadFalse(recipient);

            if(messageCount >= mailboxLimit){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send message: Recipient's mailbox is full");
            }

            // HTML sanitization policy to protect against XSS attacks
            String sanitizedContent = sanitizer.sanitize(content);

            Message msg = new Message();
            msg.setSenderId(sender);
            msg.setRecipientId(recipient);
            msg.setContent(sanitizedContent);
            msg.setTimestamp(LocalDateTime.now());

            return messageRepository.save(msg);
        }
    }

    /**
     * Retrieves paginated messages for a recipient and marks them as read using bulk update.
     * Uses atomic database operation to prevent race conditions.
     *
     * @param recipientId the recipient username
     * @param pageable pagination parameters
     * @return page of messages (updated to read status)
     */
    @Transactional
    public Page<Message> readAndMarkAsRead(String recipientId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByRecipientId(recipientId, pageable);

        if (!messages.isEmpty()) {
            List<Long> unreadMessageIds = messages.getContent().stream()
                    .filter(msg -> !msg.isRead())
                    .map(Message::getId)
                    .toList();

            if (!unreadMessageIds.isEmpty()) {
                // Bulk update (for all messages instantly in DB)
                messageRepository.markMessagesAsRead(unreadMessageIds);

                // Update in-memory messages to reflect DB changes
                messages.getContent().forEach(msg -> {
                    if (unreadMessageIds.contains(msg.getId())) {
                        msg.setRead(true);
                    }
                });
            }
        }

        return messages;
    }

    /**
     * Marks a single message as read for the specified recipient.
     * Verifies that the recipient owns the message before updating.
     *
     * @param id the message ID to mark as read
     * @param recipientId the username of the recipient
     * @throws ResponseStatusException if message not found (404) or access denied (403)
     */
    @Transactional
    public void markAsRead(Long id, String recipientId) {
        Message msg = messageRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource unavailable"));

        if(!msg.getRecipientId().equals(recipientId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this message");
        }

        if(!msg.isRead()){
            msg.setRead(true);
            messageRepository.save(msg);
        }
    }

    /**
     * Searches messages using PostgreSQL full-text search.
     * Returns messages where the user is either sender or recipient.
     *
     * @param username the user performing the search
     * @param phrase the search phrase (minimum 2 characters required)
     * @param pageable pagination parameters
     * @return page of messages matching the search phrase, ranked by relevance
     * @throws ResponseStatusException if phrase is too short (400)
     */
    @Transactional
    public Page<Message>searchMessages(String username, String phrase, Pageable pageable){
        if(phrase == null || phrase.trim().length() < 2){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query too short");
        }
        return  messageRepository.searchMessages(username, phrase, pageable);
    }
}
