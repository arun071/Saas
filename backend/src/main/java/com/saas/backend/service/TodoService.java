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
import com.saas.backend.util.SnowflakeIdGenerator;

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
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * Creates a new todo item.
     *
     * @param request The todo creation details.
     * @return The created TodoDto.
     */
    public TodoDto createTodo(TodoCreateRequest request) {
        String schemaName = TenantContext.getCurrentTenant();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Todo todo = Todo.builder()
                .id(snowflakeIdGenerator.nextId())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TodoStatus.TODO)
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

    /**
     * Lists todos for a specific project.
     *
     * @param projectId The project ID.
     * @param pageable  Pagination.
     * @return Page of TodoDtos.
     */
    public Page<TodoDto> listTodosByProject(Long projectId, Pageable pageable) {
        return todoRepository.findByProjectId(projectId, pageable)
                .map(entityMapper::toDto);
    }

    /**
     * Updates the status of a todo.
     *
     * @param id     The todo ID.
     * @param status The new status.
     * @return The updated TodoDto.
     */
    public TodoDto updateStatus(Long id, TodoStatus status) {
        String schemaName = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setStatus(status);
        Todo updated = todoRepository.save(todo);
        log.info("Updated status to {} for todo ID: {} in schema: {}", status, id, schemaName);
        return entityMapper.toDto(updated);
    }

    /**
     * Deletes a todo.
     *
     * @param id The todo ID.
     */
    public void deleteTodo(Long id) {
        String schemaName = TenantContext.getCurrentTenant();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todoRepository.delete(todo);
        log.info("Deleted todo ID: {} in schema: {}", id, schemaName);
    }

    /**
     * Lists todos with optional filtering by project, user, or status.
     *
     * @param projectId      Optional project filter.
     * @param assignedUserId Optional user filter.
     * @param status         Optional status filter.
     * @param pageable       Pagination.
     * @return Page of TodoDtos.
     */
    public Page<TodoDto> listTodosWithFilters(Long projectId, Long assignedUserId, TodoStatus status,
            Pageable pageable) {
        // Priority: Project > User > Status > All
        if (projectId != null) {
            return todoRepository.findByProjectId(projectId, pageable).map(entityMapper::toDto);
        }
        if (assignedUserId != null) {
            return todoRepository.findByAssignedUserId(assignedUserId, pageable).map(entityMapper::toDto);
        }
        if (status != null) {
            return todoRepository.findByStatus(status, pageable).map(entityMapper::toDto);
        }
        return todoRepository.findAll(pageable).map(entityMapper::toDto);
    }

    /**
     * Updates an existing todo item.
     *
     * @param id      The todo ID.
     * @param request The updated details.
     * @return The updated TodoDto.
     */
    public TodoDto updateTodo(Long id, TodoCreateRequest request) {
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
        } else {
            todo.setAssignedUser(null);
        }

        Todo updated = todoRepository.save(todo);
        log.info("Updated todo ID: {} in schema: {}", id, schemaName);
        return entityMapper.toDto(updated);
    }
}
