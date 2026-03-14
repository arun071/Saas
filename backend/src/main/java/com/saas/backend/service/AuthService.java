package com.saas.backend.service;

import com.saas.backend.dto.AuthDtos.*;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.User;
import com.saas.backend.enums.Role;
import com.saas.backend.repository.OrganizationRepository;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        /**
         * Registers a new organization and its primary administrator.
         * 
         * @param request Contains organization name, admin details, and password.
         * @return AuthResponse containing the JWT token and user info.
         */
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // Create Organization
                Organization organization = Organization.builder()
                                .name(request.getOrganizationName())
                                .build();
                organization = organizationRepository.save(organization);
                log.info("Registered new organization: {} (ID: {})", organization.getName(), organization.getId());

                // Create Admin User
                User admin = User.builder()
                                .name(request.getAdminName())
                                .email(request.getAdminEmail())
                                .password(passwordEncoder.encode(request.getAdminPassword()))
                                .role(Role.ORG_ADMIN)
                                .organization(organization)
                                .build();
                userRepository.save(admin);
                log.info("Registered new admin user: {} for organization: {}", admin.getEmail(),
                                organization.getName());

                var userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(admin.getEmail())
                                .password(admin.getPassword())
                                .authorities(admin.getRole().name())
                                .build();

                String jwtToken = jwtUtils.generateToken(userDetails, organization.getId().toString());

                return AuthResponse.builder()
                                .token(jwtToken)
                                .email(admin.getEmail())
                                .name(admin.getName())
                                .role(admin.getRole().name())
                                .organizationId(organization.getId().toString())
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                var userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(user.getRole().name())
                                .build();

                String jwtToken = jwtUtils.generateToken(userDetails, user.getOrganization().getId().toString());

                log.info("User {} successfully logged in for organization ID: {}", user.getEmail(),
                                user.getOrganization().getId());

                return AuthResponse.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .organizationId(user.getOrganization().getId().toString())
                                .build();
        }
}
