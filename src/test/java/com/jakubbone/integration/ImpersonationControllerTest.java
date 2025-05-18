package com.jakubbone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakubbone.model.User;
import com.jakubbone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ImpersonationControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        userRepository.deleteAll();
        User testUser = new User("testUser", passwordEncoder.encode("testPassword"), "USER");
        User testAdmin = new User("testAdmin", passwordEncoder.encode("testPassword"), "ADMIN");
        userRepository.save(testUser);
        userRepository.save(testAdmin);
    }
}
