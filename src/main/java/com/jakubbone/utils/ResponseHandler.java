package com.jakubbone.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

public class ResponseHandler {

    // envelope for status OK (200)
    public static ResponseEntity<Map<String,Object>> success(Object data) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "statusCode", HttpStatus.OK.value(),
                "data", data
        );
        return ResponseEntity.ok(body);
    }

    // envelope for another status (e.g. CREATED)
    public static ResponseEntity<Map<String,Object>> success(HttpStatus status, Object data) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "statusCode",    status.value(),
                "status",    status.getReasonPhrase(),
                "data",      data
        );
        return ResponseEntity.status(status).body(body);
    }

    // envelope for error (HttpStatus)
    public static ResponseEntity<Map<String,Object>> error(HttpStatus status, String message) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "errorCode", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
        return ResponseEntity.status(status).body(body);
    }

    // envelope for error (HttpStatusCode)
    public static ResponseEntity<Map<String, Object>> error(HttpStatusCode statusCode, String message) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "errorCode", statusCode.value(),
                "message", message
        );
        return ResponseEntity.status(statusCode).body(body);
    }
}