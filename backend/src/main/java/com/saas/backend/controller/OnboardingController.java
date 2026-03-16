package com.saas.backend.controller;

import com.saas.backend.dto.AuthDtos.AuthResponse;
import com.saas.backend.service.OnboardingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user onboarding.
 * Handles organization creation and joining existing organizations for users
 * who are already authenticated (e.g., via Google).
 */
@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class OnboardingController {

    private final OnboardingService onboardingService;

    /**
     * Request DTO for creating a new organization during onboarding.
     */
    @Data
    public static class CreateOrgRequest {
        private String organizationName;
    }

    /**
     * Request DTO for joining an existing organization via invite code or ID.
     */
    @Data
    public static class JoinOrgRequest {
        private String inviteCode;
        private Long organizationId;
    }

    /**
     * Creates a new organization for the currently authenticated user.
     * The user becomes the ORG_ADMIN of the new organization.
     *
     * @param request The organization name.
     * @return AuthResponse with updated token containing the new organization
     *         context.
     */
    @PostMapping("/create-org")
    public ResponseEntity<AuthResponse> createOrganization(@RequestBody CreateOrgRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(onboardingService.createOrganization(email, request.getOrganizationName()));
    }

    /**
     * Links the current user to an existing organization.
     *
     * @param request The invite code or organization ID.
     * @return AuthResponse with updated token.
     */
    @PostMapping("/join-org")
    public ResponseEntity<AuthResponse> joinOrganization(@RequestBody JoinOrgRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .ok(onboardingService.joinOrganization(email, request.getInviteCode(), request.getOrganizationId()));
    }
}
