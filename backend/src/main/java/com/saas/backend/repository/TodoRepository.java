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
    Page<Todo> findByProjectId(UUID projectId, Pageable pageable);

    Page<Todo> findByAssignedUserId(UUID assignedUserId, Pageable pageable);

    Page<Todo> findByStatus(TodoStatus status, Pageable pageable);
}
