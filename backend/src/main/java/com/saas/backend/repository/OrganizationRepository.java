package com.saas.backend.repository;

import com.saas.backend.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing organizations (tenants) in the master database.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
