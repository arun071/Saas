package com.saas.backend.repository;

import com.saas.backend.entity.Todo;
import com.saas.backend.enums.TodoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing todo items.
 * Queries are executed against the current tenant's schema.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    /**
     * Finds todos belonging to a project.
     *
     * @param projectId The project ID.
     * @param pageable  Pagination.
     * @return Page of Todos.
     */
    Page<Todo> findByProjectId(Long projectId, Pageable pageable);

    /**
     * Finds todos assigned to a specific user.
     *
     * @param assignedUserId The user ID.
     * @param pageable       Pagination.
     * @return Page of Todos.
     */
    Page<Todo> findByAssignedUserId(Long assignedUserId, Pageable pageable);

    /**
     * Finds todos with a specific status.
     *
     * @param status   The status to filter by.
     * @param pageable Pagination.
     * @return Page of Todos.
     */
    Page<Todo> findByStatus(TodoStatus status, Pageable pageable);
}
