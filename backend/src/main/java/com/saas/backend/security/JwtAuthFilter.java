package com.saas.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
//        String token = extractToken(req);
//        Claims claims = jwtService.parse(token);
//
//        String tenantId = claims.get("orgId", String.class);
//        String userId   = claims.getSubject();

//        TenantContext.setTenantId(tenantId);
        // also set SecurityContext with userId...

        try {
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear(); // critical — prevent thread reuse leaking tenant
        }
    }
}