package com.jakubbone.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
// or @RestControllerAdvice -> acts as @ControllerAdvice + @ResponseBody for all methods
@Log4j2
public class GlobalExceptionHandler {

    // Handles JWT-related exceptions (e.g. invalid or expired token)
    // HTTP Status: 401 Unauthorized
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException e){
        log.error("JWT error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
    }

    // Handles invalid method arguments (e.g. illegal or unexpected input)
    // HTTP Status: 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("Illegal argument exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid argument: " + e.getMessage());
    }

    // Handles ResponseStatusException (thrown manually with a custom HTTP status)
    // HTTP Status: Defined by exception (dynamic)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e){
        log.warn("Invalid argument provided: {}", e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }

    // Handles database-related exceptions
    // HTTP Status: 500 Internal Server Error
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException e) {
        log.error("Database error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

    // Handles case when user is not found in the system (e.g. during authentication)
    // HTTP Status: 404 Not Found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UsernameNotFoundException e) {
        log.error("User not found: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // Handles validation errors for incoming request data (e.g. @Valid fails)
    // HTTP Status: 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Invalid request data: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid request data");
    }

    // Catches any other unhandled exceptions (generic)
    // HTTP Status: 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }


}
