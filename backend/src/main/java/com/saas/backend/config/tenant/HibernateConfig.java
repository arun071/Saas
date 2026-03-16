package com.saas.backend.config.tenant;

import lombok.RequiredArgsConstructor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configures Hibernate to use SCHEMA-based multi-tenancy.
 * This class sets up the JPA EntityManagerFactory with multi-tenancy support,
 * allowing the application to dynamically switch database schemas based on the
 * current tenant.
 */
@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

    private final JpaProperties jpaProperties;

    /**
     * Configures the JPA vendor adapter for Hibernate.
     *
     * @return The JpaVendorAdapter bean.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    /**
     * Configures the LocalContainerEntityManagerFactoryBean with multi-tenancy
     * settings.
     * Integrates the custom MultiTenantConnectionProvider and
     * CurrentTenantIdentifierResolver.
     *
     * @param dataSource                    The database data source.
     * @param multiTenantConnectionProvider The provider for tenant-aware
     *                                      connections.
     * @param tenantIdentifierResolver      The resolver for identifying the current
     *                                      tenant.
     * @return The EntityManagerFactory bean.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProvider<String> multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver<String> tenantIdentifierResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.saas.backend.entity");
        em.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
        jpaPropertiesMap.put("hibernate.multiTenancy", "SCHEMA");
        jpaPropertiesMap.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        jpaPropertiesMap.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);

        // Disable DDL auto for tenant schemas as they will be managed by Flyway
        jpaPropertiesMap.put("hibernate.hbm2ddl.auto", "none");

        em.setJpaPropertyMap(jpaPropertiesMap);
        return em;
    }
}
