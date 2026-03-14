package com.saas.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@lombok.extern.slf4j.Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException e) {
        log.warn("Login failed: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid email or password");
        return ResponseEntity.status(401).body(error);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Access denied: " + e.getMessage());
        return ResponseEntity.status(403).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage(), e);
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid argument: " + e.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        Map<String, String> error = new HashMap<>();
        error.put("message", "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
