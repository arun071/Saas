package com.saas.backend.repository;

import com.saas.backend.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing projects.
 * Queries are executed against the current tenant's schema.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * Finds all projects belonging to a specific workspace with pagination.
     *
     * @param workspaceId The workspace ID.
     * @param pageable    Pagination info.
     * @return A Page of projects.
     */
    Page<Project> findByWorkspaceId(Long workspaceId, Pageable pageable);
}
