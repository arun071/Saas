package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.TodoCreateRequest;
import com.saas.backend.dto.DomainDtos.TodoDto;
import com.saas.backend.enums.TodoStatus;
import com.saas.backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing todos (tasks).
 * Todos are associated with a project and can be assigned to users.
 */
@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class TodoController {

    private final TodoService todoService;

    /**
     * Creates a new todo in a specific project.
     *
     * @param request The todo creation details.
     * @return The created TodoDto.
     */
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoCreateRequest request) {
        log.info("REST request to create todo in project: {}", request.getProjectId());
        return ResponseEntity.ok(todoService.createTodo(request));
    }

    /**
     * Lists all todos for a given project.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination information.
     * @return A page of TodoDtos.
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TodoDto>> listByProject(
            @PathVariable Long projectId,
            Pageable pageable) {
        return ResponseEntity.ok(todoService.listTodosByProject(projectId, pageable));
    }

    /**
     * Updates the status of an existing todo.
     *
     * @param id     The ID of the todo.
     * @param status The new status value.
     * @return The updated TodoDto.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TodoDto> updateStatus(
            @PathVariable Long id,
            @RequestParam TodoStatus status) {
        log.info("REST request to update status of todo ID: {} to {}", id, status);
        return ResponseEntity.ok(todoService.updateStatus(id, status));
    }

    /**
     * Updates all fields of an existing todo.
     *
     * @param id      The ID of the todo.
     * @param request The updated todo data.
     * @return The updated TodoDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable Long id,
            @RequestBody TodoCreateRequest request) {
        log.info("REST request to update todo ID: {}", id);
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    /**
     * Deletes a todo by its ID.
     *
     * @param id The ID of the todo to delete.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        log.info("REST request to delete todo ID: {}", id);
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches and filters todos based on various criteria.
     *
     * @param projectId      Optional project ID filter.
     * @param assignedUserId Optional assigned user ID filter.
     * @param status         Optional status filter.
     * @param pageable       Pagination information.
     * @return A page of TodoDtos.
     */
    @GetMapping
    public ResponseEntity<Page<TodoDto>> listWithFilters(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) TodoStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(todoService.listTodosWithFilters(projectId, assignedUserId, status, pageable));
    }
}
