package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.TodoCreateRequest;
import com.saas.backend.dto.DomainDtos.TodoDto;
import com.saas.backend.entity.*;
import com.saas.backend.enums.TodoStatus;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.OrganizationRepository;
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
    private final OrganizationRepository organizationRepository;
    private final EntityMapper entityMapper;

    public TodoDto createTodo(TodoCreateRequest request) {
        UUID orgId = TenantContext.getCurrentTenant();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Security check
        if (!project.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized project access");
        }

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // Security check
            if (!assignedUser.getOrganization().getId().equals(orgId)) {
                throw new RuntimeException("Unauthorized user assignment");
            }
        }

        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .project(project)
                .assignedUser(assignedUser)
                .organization(organization)
                .dueDate(request.getDueDate())
                .build();

        todo = todoRepository.save(todo);
        log.info("Created todo: {} (ID: {}) for project: {} in organization: {}", todo.getTitle(), todo.getId(),
                todo.getProject().getId(), orgId);
        return entityMapper.toDto(todo);
    }

    public Page<TodoDto> listTodosByProject(UUID projectId, Pageable pageable) {
        UUID orgId = TenantContext.getCurrentTenant();
        return todoRepository.findByProjectIdAndOrganization_Id(projectId, orgId, pageable)
                .map(entityMapper::toDto);
    }

    public TodoDto updateStatus(UUID id, TodoStatus status) {
        UUID orgId = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        todo.setStatus(status);
        Todo updated = todoRepository.save(todo);
        log.info("Updated status to {} for todo ID: {} in organization: {}", status, id, orgId);
        return entityMapper.toDto(updated);
    }

    public void deleteTodo(UUID id) {
        UUID orgId = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        todoRepository.delete(todo);
        log.info("Deleted todo ID: {} in organization: {}", id, orgId);
    }

    public Page<TodoDto> listTodosWithFilters(UUID projectId, UUID assignedUserId, TodoStatus status,
            Pageable pageable) {
        UUID orgId = TenantContext.getCurrentTenant();
        // Simple implementation for now, can be improved with Specification
        if (projectId != null) {
            return todoRepository.findByProjectIdAndOrganization_Id(projectId, orgId, pageable)
                    .map(entityMapper::toDto);
        } else if (assignedUserId != null) {
            return todoRepository.findByAssignedUserIdAndOrganization_Id(assignedUserId, orgId, pageable)
                    .map(entityMapper::toDto);
        } else if (status != null) {
            return todoRepository.findByStatusAndOrganization_Id(status, orgId, pageable).map(entityMapper::toDto);
        } else {
            return todoRepository.findByOrganization_Id(orgId, pageable).map(entityMapper::toDto);
        }
    }

    public TodoDto updateTodo(UUID id, TodoCreateRequest request) {
        UUID orgId = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setStatus(request.getStatus());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!assignedUser.getOrganization().getId().equals(orgId)) {
                throw new RuntimeException("Unauthorized user assignment");
            }
            todo.setAssignedUser(assignedUser);
        }

        Todo updated = todoRepository.save(todo);
        log.info("Updated todo ID: {} in organization: {}", id, orgId);
        return entityMapper.toDto(updated);
    }
}
