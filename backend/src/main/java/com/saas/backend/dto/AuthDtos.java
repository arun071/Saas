package com.saas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Container for Authentication-related Data Transfer Objects.
 */
public class AuthDtos {

    /** Request DTO for Google OAuth login. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoogleLoginRequest {
        /** The identity token received from Google frontend integration. */
        @NotBlank
        private String token;
    }

    /** Request DTO for standard email/password login. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        /** User's registered email address. */
        @Email
        @NotBlank
        private String email;

        /** Plain-text password. */
        @NotBlank
        private String password;
    }

    /** Request DTO for new user and organization registration. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        /**
         * Optional name for the new organization. If provided, owner will be ORG_ADMIN.
         */
        private String organizationName;

        /** Full name of the administrator. */
        @NotBlank
        private String adminName;

        /** Email of the administrator. */
        @Email
        @NotBlank
        private String adminEmail;

        /** Password for the new account (min 6 chars). */
        @NotBlank
        @Size(min = 6)
        private String adminPassword;
    }

    /** Response DTO containing the JWT and basic user info. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        /** Short-lived JWT Bearer token. */
        private String token;
        /** User's email. */
        private String email;
        /** User's name. */
        private String name;
        /** User's system role. */
        private String role;
        /** Associated organization identifier. */
        private String organizationId;
    }
}
