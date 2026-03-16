package com.saas.backend.service;

import com.saas.backend.dto.AuthDtos.AuthResponse;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.OrganizationInvite;
import com.saas.backend.entity.User;
import com.saas.backend.enums.MembershipStatus;
import com.saas.backend.enums.Role;
import com.saas.backend.repository.OrganizationInviteRepository;
import com.saas.backend.repository.OrganizationRepository;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service dedicated to onboarding new users.
 * Handles the process of creating a new organization or joining an existing one
 * after initial authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationInviteRepository inviteRepository;
    private final TenantProvisioningService tenantProvisioningService;
    private final JwtUtils jwtUtils;

    /**
     * Provisions a new tenant and links the user as the ORG_ADMIN.
     *
     * @param email            The email of the user creating the organization.
     * @param organizationName The name of the new organization.
     * @return AuthResponse with updated token.
     */
    @Transactional
    public AuthResponse createOrganization(String email, String organizationName) {
        log.info("Starting consolidated organization creation for user: {} name: {}", email, organizationName);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (user.getOrganization() != null) {
            log.warn("User {} already belongs to organization: {}", email, user.getOrganization().getName());
            throw new RuntimeException("User already belongs to an organization");
        }

        // 1. Provision the new tenant and schema
        Organization organization = tenantProvisioningService.createTenant(organizationName);

        log.info("Tenant provisioned for {} (ID: {}). Linking user...", organizationName, organization.getId());

        // 2. Link the user to the organization
        user.setOrganization(organization);
        user.setRole(Role.ORG_ADMIN);
        user.setMembershipStatus(MembershipStatus.APPROVED);
        userRepository.save(user);

        log.info("Consolidated onboarding complete for user: {}", email);

        return buildAuthResponse(user, organization);
    }

    /**
     * Links a user to an existing organization via invite code or ID.
     *
     * @param email          The user's email.
     * @param inviteCode     The optional invite code.
     * @param organizationId The optional organization ID.
     * @return AuthResponse with updated token.
     */
    @Transactional
    public AuthResponse joinOrganization(String email, String inviteCode, Long organizationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOrganization() != null) {
            throw new RuntimeException("User already belongs to an organization");
        }

        Organization organization = null;
        MembershipStatus status = MembershipStatus.PENDING;

        if (inviteCode != null && !inviteCode.trim().isEmpty()) {
            OrganizationInvite invite = inviteRepository.findByInviteCode(inviteCode)
                    .orElseThrow(() -> new RuntimeException("Invalid invite code"));

            if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Invite code expired");
            }
            organization = invite.getOrganization();
            // Joining via invite code could bypass approval if desired
            // status = MembershipStatus.APPROVED;
        } else if (organizationId != null) {
            organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
        } else {
            throw new RuntimeException("Must provide invite code or organization ID");
        }

        user.setOrganization(organization);
        user.setRole(Role.USER);
        user.setMembershipStatus(status);
        userRepository.save(user);

        log.info("User {} requested to join organization {}", email, organization.getName());

        return buildAuthResponse(user, organization);
    }

    /**
     * Internal helper to build AuthResponse with correct tenant context.
     *
     * @param user         The user.
     * @param organization The organization.
     * @return The AuthResponse.
     */
    private AuthResponse buildAuthResponse(User user, Organization organization) {
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities(user.getRole().name())
                .build();

        String schemaName = "tenant_" + organization.getId().toString();
        String jwtToken = jwtUtils.generateToken(userDetails, organization.getId().toString(), schemaName);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .organizationId(organization.getId().toString())
                .build();
    }
}
