package com.saas.backend.security;

import com.saas.backend.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.entity.User;
import com.saas.backend.enums.MembershipStatus;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts every request to validate the JWT in the Authorization
 * header.
 * If valid, it sets the Spring Security context and the TenantContext for
 * database schema isolation.
 */
@Component
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        try {
            if (isValidAuthHeader(authHeader)) {
                String jwt = authHeader.substring(7);
                String userEmail = extractEmail(jwt);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticateUser(request, response, jwt, userEmail);
                    if (response.isCommitted())
                        return;
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7;
    }

    private String extractEmail(String jwt) {
        try {
            return jwtUtils.extractUsername(jwt);
        } catch (Exception e) {
            log.warn("Failed to extract username from JWT: {}", e.getMessage());
            return null;
        }
    }

    private void authenticateUser(HttpServletRequest request, HttpServletResponse response, String jwt,
            String userEmail) throws IOException {
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        } catch (UsernameNotFoundException e) {
            log.warn("User not found for JWT email: {}. Token may be stale.", userEmail);
            return;
        }

        if (jwtUtils.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Check if user is pending
            if (isPendingUser(request, userEmail)) {
                log.warn("Blocked pending user {} from accessing the system", userEmail);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account pending approval");
                return;
            }

            // Set TenantContext
            setTenantContext(request, jwt);
        }
    }

    private boolean isPendingUser(HttpServletRequest request, String userEmail) {
        String path = request.getRequestURI();
        if (!path.startsWith("/auth") && !path.startsWith("/api/onboarding")) {
            User currentUser = userRepository.findByEmail(userEmail).orElse(null);
            return currentUser != null && currentUser.getMembershipStatus() == MembershipStatus.PENDING;
        }
        return false;
    }

    private void setTenantContext(HttpServletRequest request, String jwt) {
        String schemaNameHeader = request.getHeader("X-Tenant-Schema");
        String schemaName = null;

        if (schemaNameHeader != null && !schemaNameHeader.isEmpty()) {
            schemaName = schemaNameHeader;
        } else {
            schemaName = jwtUtils.extractClaim(jwt, claims -> claims.get("schema_name", String.class));
        }

        if (schemaName != null) {
            TenantContext.setCurrentTenant(schemaName);
            log.debug("Identified tenant context: {}", schemaName);
        }
    }
}
