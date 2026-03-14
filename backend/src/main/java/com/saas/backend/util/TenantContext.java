package com.saas.backend.util;

/**
 * Holds the tenant identifier (Schema name) for the current thread.
 * This is populated by the JwtAuthenticationFilter and used by the
 * MultiTenantConnectionProvider to switch database schemas.
 */
public class TenantContext {
    public static final String DEFAULT_TENANT = "saas_db";
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return currentTenant.get() != null ? currentTenant.get() : DEFAULT_TENANT;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
