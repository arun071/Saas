package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.UserDto;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.OrganizationInvite;
import com.saas.backend.entity.User;
import com.saas.backend.enums.MembershipStatus;
import com.saas.backend.enums.Role;
import com.saas.backend.repository.OrganizationInviteRepository;
import com.saas.backend.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for organization administrators to manage their users.
 * Provides endpoints for approving/rejecting members and generating invites.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AdminOnboardingController {

    private final UserRepository userRepository;
    private final OrganizationInviteRepository inviteRepository;

    /**
     * Response DTO for generated organization invites.
     */
    @Data
    public static class InviteResponse {
        private String inviteCode;
        private LocalDateTime expiresAt;
    }

    /**
     * Helper method to get the current authenticated user and verify they are an
     * administrator.
     *
     * @return The authenticated User entity.
     * @throws RuntimeException If admin is not found or lacks ORG_ADMIN role.
     */
    private User getAuthenticatedAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Role.ORG_ADMIN) {
            throw new RuntimeException("Only ORG_ADMIN can perform this action");
        }
        return admin;
    }

    /**
     * Retrieves a list of users who have requested to join the organization but are
     * pending approval.
     *
     * @return List of pending UserDtos.
     */
    @GetMapping("/users/pending")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<UserDto>> getPendingUsers() {
        User admin = getAuthenticatedAdmin();
        Organization org = admin.getOrganization();

        List<User> pendingUsers = userRepository.findByOrganizationIdAndMembershipStatus(org.getId(),
                MembershipStatus.PENDING);

        List<UserDto> dtos = pendingUsers.stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .organizationId(u.getOrganization().getId())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Directly adds a user to the organization (approves them and assigns USER
     * role).
     *
     * @param email The email of the user to add.
     * @return 200 OK on success.
     */
    @PostMapping("/users/add")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Transactional
    public ResponseEntity<?> addUserToOrganization(@RequestBody String email) {
        User admin = getAuthenticatedAdmin();
        Organization org = admin.getOrganization();

        // Email might be sent as a raw string or in a JSON object depending on client
        String targetEmail = email.contains("@") ? email.replace("\"", "").trim() : email;

        User targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + targetEmail));

        if (targetUser.getOrganization() != null) {
            throw new RuntimeException("User already belongs to an organization");
        }

        targetUser.setOrganization(org);
        targetUser.setRole(Role.USER);
        targetUser.setMembershipStatus(MembershipStatus.APPROVED);
        userRepository.save(targetUser);

        log.info("Admin {} added user {} directly to organization {}", admin.getEmail(), targetUser.getEmail(),
                org.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Approves a user's pending membership request.
     *
     * @param userId The ID of the user to approve.
     * @return 200 OK on success.
     */
    @PostMapping("/users/{userId}/approve")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Transactional
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        User admin = getAuthenticatedAdmin();
        Organization org = admin.getOrganization();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (targetUser.getOrganization() == null || !targetUser.getOrganization().getId().equals(org.getId())) {
            throw new RuntimeException("User not part of this organization");
        }

        targetUser.setMembershipStatus(MembershipStatus.APPROVED);
        userRepository.save(targetUser);

        log.info("Admin {} approved user {} for organization {}", admin.getEmail(), targetUser.getEmail(),
                org.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Rejects a user's pending membership request.
     *
     * @param userId The ID of the user to reject.
     * @return 200 OK on success.
     */
    @PostMapping("/users/{userId}/reject")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Transactional
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {
        User admin = getAuthenticatedAdmin();
        Organization org = admin.getOrganization();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (targetUser.getOrganization() == null || !targetUser.getOrganization().getId().equals(org.getId())) {
            throw new RuntimeException("User not part of this organization");
        }

        // Remove from org
        targetUser.setOrganization(null);
        userRepository.save(targetUser);

        log.info("Admin {} rejected user {} from organization {}", admin.getEmail(), targetUser.getEmail(),
                org.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Generates a new unique invite code for the organization.
     *
     * @return InviteResponse containing the code and expiry date.
     */
    @PostMapping("/invites/generate")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    @Transactional
    public ResponseEntity<InviteResponse> generateInvite() {
        User admin = getAuthenticatedAdmin();
        Organization org = admin.getOrganization();

        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        OrganizationInvite invite = OrganizationInvite.builder()
                .organization(org)
                .createdBy(admin)
                .inviteCode(code)
                .expiresAt(LocalDateTime.now().plusDays(7)) // 7 day expiry
                .build();

        inviteRepository.save(invite);

        InviteResponse response = new InviteResponse();
        response.setInviteCode(code);
        response.setExpiresAt(invite.getExpiresAt());

        return ResponseEntity.ok(response);
    }
}
