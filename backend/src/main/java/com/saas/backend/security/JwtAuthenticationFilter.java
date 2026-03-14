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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                jwt = authHeader.substring(7);
                try {
                    userEmail = jwtUtils.extractUsername(jwt);
                } catch (Exception e) {
                    log.warn("Failed to extract username from JWT: {}", e.getMessage());
                    filterChain.doFilter(request, response);
                    return;
                }

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails;
                    try {
                        userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    } catch (UsernameNotFoundException e) {
                        log.warn("User not found for JWT email: {}. Token may be stale.", userEmail);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if (jwtUtils.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Set TenantContext from JWT claim or header
                        String tenantId = request.getHeader("X-Tenant-ID");
                        String orgId = null;

                        if (tenantId != null && !tenantId.isEmpty()) {
                            orgId = tenantId;
                            log.debug("Tenant ID from header: {}", orgId);
                        } else {
                            orgId = jwtUtils.extractClaim(jwt, claims -> claims.get("org_id", String.class));
                            if (orgId != null) {
                                log.debug("Tenant ID from JWT: {}", orgId);
                            }
                        }

                        if (orgId != null) {
                            String schemaName = "tenant_" + orgId.replace("-", "_");
                            TenantContext.setCurrentTenant(schemaName);
                            log.debug("Identified tenant context: {} (Schema: {})", orgId, schemaName);
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
