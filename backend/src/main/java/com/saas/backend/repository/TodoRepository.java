package com.saas.backend.repository;

import com.saas.backend.entity.Todo;
import com.saas.backend.enums.TodoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    Page<Todo> findByProjectIdAndOrganization_Id(UUID projectId, UUID organizationId, Pageable pageable);

    Page<Todo> findByAssignedUserIdAndOrganization_Id(UUID assignedUserId, UUID organizationId, Pageable pageable);

    Page<Todo> findByStatusAndOrganization_Id(TodoStatus status, UUID organizationId, Pageable pageable);

    Page<Todo> findByOrganization_Id(UUID organizationId, Pageable pageable);
}
