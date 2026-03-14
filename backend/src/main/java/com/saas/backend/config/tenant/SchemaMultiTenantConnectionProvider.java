package com.saas.backend.config.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides database connections for different tenants by switching databases.
 * Uses setCatalog() instead of setSchema() because MySQL treats schemas as
 * catalogs (databases), and setSchema() is a no-op in MySQL Connector/J.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;
    private static final String DEFAULT_SCHEMA = "saas_db";

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.debug("Getting connection for tenant: {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        try {
            // MySQL uses catalogs (databases), not schemas. setSchema() is a no-op.
            connection.setCatalog(tenantIdentifier);
        } catch (SQLException e) {
            log.error("Could not switch to database {}. Falling back to default.", tenantIdentifier);
            connection.setCatalog(DEFAULT_SCHEMA);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        log.debug("Releasing connection for tenant: {}", tenantIdentifier);
        try {
            connection.setCatalog(DEFAULT_SCHEMA);
        } catch (SQLException e) {
            log.warn("Could not reset database to default.");
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
