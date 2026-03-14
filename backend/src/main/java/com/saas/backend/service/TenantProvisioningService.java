package com.saas.backend.service;

import com.saas.backend.entity.Organization;
import com.saas.backend.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Service for provisioning new tenants (Organizations).
 * Creates a new database schema and runs Flyway migrations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantProvisioningService {

    private final OrganizationRepository organizationRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Organization createTenant(String organizationName) {
        log.info("Provisioning new tenant: {}", organizationName);

        // 1. Create Organization metadata in master schema
        Organization organization = Organization.builder()
                .name(organizationName)
                .build();
        organization = organizationRepository.save(organization);

        String orgId = organization.getId().toString();
        String schemaName = "tenant_" + orgId.replace("-", "_");

        // 2. Create the schema in MySQL
        log.info("Creating database schema: {} for organization: {}", schemaName, organizationName);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

        // 3. Run Flyway migrations on the new schema
        log.info("Running Flyway migrations for schema: {}", schemaName);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("db/migration/tenant")
                .load();
        flyway.migrate();

        log.info("Tenant provisioned successfully: {} (Schema: {})", organizationName, schemaName);
        return organization;
    }
}
