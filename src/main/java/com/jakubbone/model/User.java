package com.jakubbone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor // JPA/Hibernate require to create object getting from DB
public class User {

    public enum Role { USER, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Hibernate save as a String, not a number
    private Role role;

    @Column(nullable = true)
    private String provider;

    public User(String username, String role, String provider ) {
        this.username = username;
        this.role = Role.valueOf(role);
        this.provider = provider;
    }
}
