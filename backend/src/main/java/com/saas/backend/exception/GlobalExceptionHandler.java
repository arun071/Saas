package com.saas.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for the entire application.
 * Intercepts exceptions thrown by controllers and returns standardized JSON
 * error responses.
 */
@RestControllerAdvice
@lombok.extern.slf4j.Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles authentication failures.
     * 
     * @param e BadCredentialsException.
     * @return 401 Unauthorized response.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException e) {
        log.warn("Login failed: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid email or password");
        return ResponseEntity.status(401).body(error);
    }

    /**
     * Handles authorization failures (RBAC).
     * 
     * @param e AccessDeniedException.
     * @return 403 Forbidden response.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Access denied: " + e.getMessage());
        return ResponseEntity.status(403).body(error);
    }

    /**
     * Handles general runtime exceptions.
     * 
     * @param e RuntimeException.
     * @return 500 Internal Server Error response.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage(), e);
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(500).body(error);
    }

    /**
     * Handles invalid user input.
     * 
     * @param e IllegalArgumentException.
     * @return 400 Bad Request response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid argument: " + e.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    /**
     * Catch-all handler for unexpected checked exceptions.
     * 
     * @param e Exception.
     * @return 500 Internal Server Error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        Map<String, String> error = new HashMap<>();
        error.put("message", "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}
