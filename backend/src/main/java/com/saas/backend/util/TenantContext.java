package com.saas.backend.util;

import java.util.UUID;

/**
 * Holds the organization context (Tenant ID) for the current thread.
 * This is populated by the JwtAuthenticationFilter and used by services
 * to ensure database operations are isolated locally to the user's
 * organization.
 */
public class TenantContext {
    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
