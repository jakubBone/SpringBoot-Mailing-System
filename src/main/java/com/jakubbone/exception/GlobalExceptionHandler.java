package com.jakubbone.exception;

import com.jakubbone.dto.ErrorResponse;
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

import java.time.Instant;

@ControllerAdvice
// or @RestControllerAdvice -> acts as @ControllerAdvice + @ResponseBody for all methods
@Log4j2
public class GlobalExceptionHandler {

    // Handles JWT-related exceptions (e.g. invalid or expired token)
    // HTTP Status: 401 Unauthorized
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e){
        log.error("JWT error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid JWT token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Handles invalid method arguments (e.g. illegal or unexpected input)
    // HTTP Status: 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("Illegal argument exception: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Invalid argument"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handles ResponseStatusException (thrown manually with a custom HTTP status)
    // HTTP Status: Defined by exception (dynamic)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e){
        log.warn("Unexpected error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                e.getStatusCode().value(),
                e.getStatusCode().toString(),
                e.getReason() != null ? e.getReason() : "Unexpected error"
        );
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    // Handles database-related exceptions
    // HTTP Status: 500 Internal Server Error
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("Database error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Handles case when user is not found in the system (e.g. during authentication)
    // HTTP Status: 404 Not Found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
        log.error("User not found: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "User not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handles validation errors for incoming request data (e.g. @Valid fails)
    // HTTP Status: 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Invalid request data: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Invalid request data"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Catches any other unhandled exceptions (generic)
    // HTTP Status: 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
