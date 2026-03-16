package com.saas.backend.repository;

import com.saas.backend.entity.OrganizationInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing organization invites.
 */
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {
    /**
     * Finds an invite by its unique alphanumeric code.
     *
     * @param inviteCode The code to search for.
     * @return An Optional containing the invite if found.
     */
    Optional<OrganizationInvite> findByInviteCode(String inviteCode);
}
