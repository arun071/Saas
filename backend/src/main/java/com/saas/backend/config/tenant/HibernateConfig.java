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
 */
@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

    private final JpaProperties jpaProperties;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

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
