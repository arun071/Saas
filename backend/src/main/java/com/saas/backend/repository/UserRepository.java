package com.saas.backend.repository;

import com.saas.backend.enums.MembershipStatus;
import com.saas.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing users.
 * This repository accesses the master database (saas_db).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their unique email.
     *
     * @param email The email address.
     * @return Optional containing the user.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users belonging to an organization with a specific membership status.
     *
     * @param organizationId The organization ID.
     * @param status         The membership status.
     * @return List of users.
     */
    List<User> findByOrganizationIdAndMembershipStatus(Long organizationId, MembershipStatus status);
}
