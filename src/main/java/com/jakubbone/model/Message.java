package com.jakubbone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor // JPA/Hibernate require to create object getting from DB
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated
    private Long id;

    @ManyToOne(optional = false) // Relation to User Entity
    @JoinColumn(name = "senderId")
    private User sender;

    @ManyToOne(optional = false) // Relation to User Entity
    @JoinColumn(name = "recipientId")
    private User recipient;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
