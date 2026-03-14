package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.TodoCreateRequest;
import com.saas.backend.dto.DomainDtos.TodoDto;
import com.saas.backend.entity.*;
import com.saas.backend.enums.TodoStatus;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.ProjectRepository;
import com.saas.backend.repository.TodoRepository;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing individual tasks (Todos) within a project.
 * Enforces organization-level isolation and optionally links tasks to users.
 */
@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
@lombok.extern.slf4j.Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    public TodoDto createTodo(TodoCreateRequest request) {
        String schemaName = TenantContext.getCurrentTenant();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // Note: Since users are in master schema, cross-schema checks might be needed
            // but for now we assume valid ID means valid access if retrieved.
        }

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .project(project)
                .assignedUser(assignedUser)
                .dueDate(request.getDueDate())
                .build();

        todo = todoRepository.save(todo);
        log.info("Created todo: {} (ID: {}) for project: {} in schema: {}", todo.getTitle(), todo.getId(),
                todo.getProject().getId(), schemaName);
        return entityMapper.toDto(todo);
    }

    public Page<TodoDto> listTodosByProject(UUID projectId, Pageable pageable) {
        return todoRepository.findByProjectId(projectId, pageable)
                .map(entityMapper::toDto);
    }

    public TodoDto updateStatus(UUID id, TodoStatus status) {
        String schemaName = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setStatus(status);
        Todo updated = todoRepository.save(todo);
        log.info("Updated status to {} for todo ID: {} in schema: {}", status, id, schemaName);
        return entityMapper.toDto(updated);
    }

    public void deleteTodo(UUID id) {
        String schemaName = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todoRepository.delete(todo);
        log.info("Deleted todo ID: {} in schema: {}", id, schemaName);
    }

    public Page<TodoDto> listTodosWithFilters(UUID projectId, UUID assignedUserId, TodoStatus status,
            Pageable pageable) {
        if (projectId != null) {
            return todoRepository.findByProjectId(projectId, pageable)
                    .map(entityMapper::toDto);
        } else if (assignedUserId != null) {
            return todoRepository.findByAssignedUserId(assignedUserId, pageable)
                    .map(entityMapper::toDto);
        } else if (status != null) {
            return todoRepository.findByStatus(status, pageable).map(entityMapper::toDto);
        } else {
            return todoRepository.findAll(pageable).map(entityMapper::toDto);
        }
    }

    public TodoDto updateTodo(UUID id, TodoCreateRequest request) {
        String schemaName = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setStatus(request.getStatus());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            todo.setAssignedUser(assignedUser);
        }

        Todo updated = todoRepository.save(todo);
        log.info("Updated todo ID: {} in schema: {}", id, schemaName);
        return entityMapper.toDto(updated);
    }
}
