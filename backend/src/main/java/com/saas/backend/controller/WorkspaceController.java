package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.WorkspaceDto;
import com.saas.backend.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceDto> createWorkspace(@RequestBody Map<String, String> request) {
        log.info("REST request to create workspace: {}", request.get("name"));
        return ResponseEntity.ok(workspaceService.createWorkspace(request.get("name")));
    }

    @GetMapping
    public ResponseEntity<Page<WorkspaceDto>> listWorkspaces(Pageable pageable) {
        return ResponseEntity.ok(workspaceService.listWorkspaces(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDto> updateWorkspace(@PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        log.info("REST request to update workspace ID: {}", id);
        return ResponseEntity.ok(workspaceService.updateWorkspace(id, request.get("name")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable UUID id) {
        log.info("REST request to delete workspace ID: {}", id);
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
