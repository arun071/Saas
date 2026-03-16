package com.saas.backend.config.tenant;

import com.saas.backend.util.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Resolves the tenant identifier from the TenantContext.
 * This is used by Hibernate to determine which tenant is associated with the
 * current session/thread.
 *
 * It retrieves the schema name set by the {@code JwtAuthenticationFilter}.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    /**
     * Resolves the current tenant identifier from the ThreadLocal context.
     *
     * @return The current tenant identifier (database schema name).
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getCurrentTenant();
    }

    /**
     * Specifies whether existing sessions should be validated against the current
     * tenant.
     *
     * @return Always true for this implementation.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
