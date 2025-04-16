package com.jakubbone.controller;

import com.jakubbone.dto.LoginRequest;
import com.jakubbone.model.User;
import com.jakubbone.reposotory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/")
public class LoginController {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public LoginController(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()){
            // Return a generic HTTP 401 Unauthorized response without specifying
            // whether the username or the password is incorrect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        User user = userOpt.get();

        if (!encoder.matches(password, user.getPasswordHash())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }


        /*String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());
        Map<String, String> responseBody = Collections.singletonMap("token", token);
        return ResponseEntity.ok(responseBody);*/
        return ResponseEntity.status(HttpStatus.OK).body("OK testing response");
    }
}
