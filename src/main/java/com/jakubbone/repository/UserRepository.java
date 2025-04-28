package com.jakubbone.repository;

import com.jakubbone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JPARepository provides the ready methods
// e.g. save(), findById(), findAll(), delete()
// for User Entity with the key type Long
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
