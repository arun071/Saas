package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.WorkspaceDto;
import com.saas.backend.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing workspaces.
 * Workspaces are top-level containers for projects and todos within an
 * organization.
 */
@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * Creates a new workspace.
     *
     * @param request Map containing the workspace "name".
     * @return The created WorkspaceDto.
     */
    @PostMapping
    public ResponseEntity<WorkspaceDto> createWorkspace(@RequestBody Map<String, String> request) {
        log.info("REST request to create workspace: {}", request.get("name"));
        return ResponseEntity.ok(workspaceService.createWorkspace(request.get("name")));
    }

    /**
     * Lists all workspaces accessible to the current user.
     *
     * @param pageable Pagination and sorting information.
     * @return A page of WorkspaceDtos.
     */
    @GetMapping
    public ResponseEntity<Page<WorkspaceDto>> listWorkspaces(Pageable pageable) {
        return ResponseEntity.ok(workspaceService.listWorkspaces(pageable));
    }

    /**
     * Updates an existing workspace.
     *
     * @param id      The ID of the workspace.
     * @param request Map containing updated fields (e.g., name).
     * @return The updated WorkspaceDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDto> updateWorkspace(@PathVariable Long id,
            @RequestBody Map<String, String> request) {
        log.info("REST request to update workspace ID: {}", id);
        return ResponseEntity.ok(workspaceService.updateWorkspace(id, request.get("name")));
    }

    /**
     * Deletes a workspace and its associated data.
     *
     * @param id The ID of the workspace to delete.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        log.info("REST request to delete workspace ID: {}", id);
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
