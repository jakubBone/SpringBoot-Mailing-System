package com.jakubbone.exception;

import com.jakubbone.dto.ErrorResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.springframework.http.HttpStatus.*;


@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * Handles JWT-related exceptions such as invalid or expired tokens.
     *
     * @param e the JWT exception
     * @return error response with HTTP 401 Unauthorized
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e){
        log.error("JWT error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                UNAUTHORIZED.value(),
                UNAUTHORIZED.getReasonPhrase(),
                "Invalid JWT token"
        );
        return ResponseEntity.status(UNAUTHORIZED).body(error);
    }

    /**
     * Handles illegal method arguments.
     *
     * @param e the illegal argument exception
     * @return error response with HTTP 400 Bad Request
     */t
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("Illegal argument exception: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                "Invalid argument"
        );
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    /**
     * Handles custom exceptions thrown with specific HTTP status codes.
     *
     * @param e the response status exception
     * @return error response with the HTTP status from the exception
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e){
        log.warn("Unexpected error occurred: {}", e.getMessage());

        String reason = switch (e.getStatusCode()) {
            case NOT_FOUND     -> "Resource not available";
            case CONFLICT      -> "Request conflict";
            case BAD_REQUEST   -> "Invalid request";
            default            -> "Request failed";
        };

        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                e.getStatusCode().value(),
                e.getStatusCode().toString(),
                e.getReason() != null ? e.getReason() : reason
        );
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    /**
     * Handles database-related exceptions.
     *
     * @param e the data access exception
     * @return error response with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        log.error("Database error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error"
        );
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles username not found during authentication.
     *
     * @param e the username not found exception
     * @return error response with HTTP 401 Unauthorized
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException e) {
        log.error("User not found: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                UNAUTHORIZED.value(),
                UNAUTHORIZED.getReasonPhrase(),
                "Invalid credentials"
        );
        return ResponseEntity.status(UNAUTHORIZED).body(error);
    }

    /**
     * Handles validation errors for incoming request data (triggered by @Valid).
     *
     * @param e the method argument not valid exception
     * @return error response with HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Invalid request data: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                "Invalid request data"
        );
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    /**
     * Catches all other unhandled exceptions.
     * This is a fallback handler to prevent exposing stack traces to clients.
     *
     * @param e the generic exception
     * @return error response with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected error occurred"
        );
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

}