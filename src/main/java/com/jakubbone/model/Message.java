package com.jakubbone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    private Long id;
    private User sender;
    private User recipient;
    private String content;
    private LocalDateTime timestamp;
}
