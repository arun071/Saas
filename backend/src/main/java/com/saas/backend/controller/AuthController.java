package com.saas.backend.controller;

import com.saas.backend.dto.AuthDtos.*;
import com.saas.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST controller for authentication and registration operations.
 * Handles user login, organization registration, and Google OAuth2 login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@lombok.extern.slf4j.Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new organization and its administrator.
     *
     * @param request The registration details (Org name, Admin info).
     * @return AuthResponse containing the JWT token and user details.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register new organization: {}", request.getOrganizationName());
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates an existing user via email and password.
     *
     * @param request The login credentials.
     * @return AuthResponse containing the JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Authenticates a user via Google OAuth2 token.
     *
     * @param request The Google ID token.
     * @return AuthResponse containing the JWT token.
     */
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        log.info("REST request for Google Login");
        return ResponseEntity.ok(authService.googleLogin(request));
    }
}
