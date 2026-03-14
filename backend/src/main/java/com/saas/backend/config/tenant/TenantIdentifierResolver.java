package com.saas.backend.config.tenant;

import com.saas.backend.util.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Resolves the tenant identifier from the TenantContext.
 * This is used by Hibernate to determine which tenant is associated with the
 * current session.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
