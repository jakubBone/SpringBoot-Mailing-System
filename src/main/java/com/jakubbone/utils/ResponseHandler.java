package com.jakubbone.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;

public class ResponseHandler {

    public static ResponseEntity<Map<String,Object>> success(Object data) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.OK.value(),
                "data", data
        );
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<Map<String,Object>> error(HttpStatus status, String message) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "message", message
        );
        return ResponseEntity.status(status).body(body);
    }
    public static ResponseEntity<Map<String, Object>> error(HttpStatusCode statusCode, String message) {
        Map<String,Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", statusCode.value(),
                "message", message
        );
        return ResponseEntity.status(statusCode).body(body);
    }
}