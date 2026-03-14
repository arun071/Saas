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

import java.util.UUID;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoCreateRequest request) {
        log.info("REST request to create todo in project: {}", request.getProjectId());
        return ResponseEntity.ok(todoService.createTodo(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TodoDto>> listByProject(
            @PathVariable UUID projectId,
            Pageable pageable) {
        return ResponseEntity.ok(todoService.listTodosByProject(projectId, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TodoDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam TodoStatus status) {
        log.info("REST request to update status of todo ID: {} to {}", id, status);
        return ResponseEntity.ok(todoService.updateStatus(id, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable UUID id,
            @RequestBody TodoCreateRequest request) {
        log.info("REST request to update todo ID: {}", id);
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
        log.info("REST request to delete todo ID: {}", id);
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TodoDto>> listWithFilters(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID assignedUserId,
            @RequestParam(required = false) TodoStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(todoService.listTodosWithFilters(projectId, assignedUserId, status, pageable));
    }
}
