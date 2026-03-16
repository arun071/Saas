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
import com.saas.backend.util.SnowflakeIdGenerator;

/**
 * Service for provisioning new tenants (Organizations).
 * Creates a new database schema and runs Flyway migrations to set up the
 * tenant's data isolated environment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantProvisioningService {

    private final OrganizationRepository organizationRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * Provisions a new tenant.
     * This method:
     * 1. Creates an organization record in the master database.
     * 2. Creates a dedicated MySQL schema for the tenant.
     * 3. Executes Flyway migrations on the new schema to create necessary tables.
     *
     * @param organizationName The name of the organization.
     * @return The created Organization entity.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Organization createTenant(String organizationName) {
        log.info("Provisioning new tenant: {}", organizationName);

        // 1. Create Organization metadata in master schema
        Organization organization = Organization.builder()
                .id(snowflakeIdGenerator.nextId())
                .name(organizationName)
                .build();
        organization = organizationRepository.saveAndFlush(organization);

        String schemaName = "tenant_" + organization.getId().toString();

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
