package com.saas.backend.service;

import com.saas.backend.dto.AuthDtos.*;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.User;
import com.saas.backend.enums.AuthProvider;
import com.saas.backend.enums.MembershipStatus;
import com.saas.backend.enums.Role;
import com.saas.backend.repository.OrganizationRepository;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.security.GoogleAuthVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.saas.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.saas.backend.util.SnowflakeIdGenerator;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user authentication, including registration of new organizations
 * and login for existing users.
 */
@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AuthService {

        private final UserRepository userRepository;
        private final OrganizationRepository organizationRepository;
        private final TenantProvisioningService tenantProvisioningService;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;
        private final SnowflakeIdGenerator snowflakeIdGenerator;
        private final GoogleAuthVerifier googleAuthVerifier;

        /**
         * Registers a new organization and its primary administrator.
         * 
         * @param request Contains organization name, admin details, and password.
         * @return AuthResponse containing the JWT token and user info.
         */
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                User user = User.builder()
                                .id(snowflakeIdGenerator.nextId())
                                .name(request.getAdminName())
                                .email(request.getAdminEmail())
                                .password(passwordEncoder.encode(request.getAdminPassword()))
                                .build();

                Organization organization = null;
                String schemaName = null;

                if (request.getOrganizationName() != null && !request.getOrganizationName().trim().isEmpty()) {
                        organization = tenantProvisioningService.createTenant(request.getOrganizationName());
                        log.info("Tenant provisioned for organization: {} (ID: {})", organization.getName(),
                                        organization.getId());

                        organization = organizationRepository.getReferenceById(organization.getId());
                        user.setOrganization(organization);
                        user.setRole(Role.ORG_ADMIN);
                        schemaName = "tenant_" + organization.getId().toString();
                } else {
                        user.setRole(Role.USER);
                }

                userRepository.save(user);
                log.info("Registered new user: {} (Role: {}, Org: {})", user.getEmail(), user.getRole(),
                                organization != null ? organization.getName() : "None");

                return buildAuthResponse(user, organization, schemaName);
        }

        /**
         * Authenticates an existing user via email and password.
         *
         * @param request The login credentials.
         * @return AuthResponse containing token and user profile.
         */
        @Transactional(readOnly = true)
        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Organization org = user.getOrganization();
                String schemaName = org != null ? "tenant_" + org.getId().toString() : null;

                log.info("User {} successfully logged in for organization: {}", user.getEmail(),
                                org != null ? org.getName() : "None");

                return buildAuthResponse(user, org, schemaName);
        }

        /**
         * Authenticates a user via Google OAuth2 token.
         *
         * @param request The Google ID token string.
         * @return AuthResponse containing token and user profile.
         */
        @Transactional
        public AuthResponse googleLogin(GoogleLoginRequest request) {
                GoogleIdToken.Payload payload = googleAuthVerifier.verify(request.getToken());
                if (payload == null) {
                        throw new RuntimeException("Invalid Google token");
                }

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String subjectId = payload.getSubject();

                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                        user = User.builder()
                                        .id(snowflakeIdGenerator.nextId())
                                        .name(name)
                                        .email(email)
                                        .authProvider(AuthProvider.GOOGLE)
                                        .authProviderId(subjectId)
                                        .role(Role.USER)
                                        .membershipStatus(MembershipStatus.APPROVED)
                                        .build();
                        userRepository.save(user);
                        log.info("Registered new Google user: {}", email);
                } else if (user.getAuthProvider() == AuthProvider.LOCAL) {
                        user.setAuthProvider(AuthProvider.GOOGLE);
                        user.setAuthProviderId(subjectId);
                        userRepository.save(user);
                        log.info("Linked Google account to existing user: {}", email);
                }

                Organization org = user.getOrganization();
                String schemaName = org != null ? "tenant_" + org.getId().toString() : null;

                return buildAuthResponse(user, org, schemaName);
        }

        /**
         * Internal helper to construct the AuthResponse and generate the JWT token.
         *
         * @param user         The authenticated user.
         * @param organization The optional organization they belong to.
         * @param schemaName   The optional database schema for the tenant.
         * @return The standard AuthResponse.
         */
        private AuthResponse buildAuthResponse(User user, Organization organization, String schemaName) {
                var userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword() != null ? user.getPassword() : "")
                                .authorities(user.getRole().name())
                                .build();

                String orgIdStr = organization != null ? organization.getId().toString() : null;
                String jwtToken = jwtUtils.generateToken(userDetails, orgIdStr, schemaName);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .organizationId(orgIdStr)
                                .build();
        }
}
