package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.ProjectCreateRequest;
import com.saas.backend.dto.DomainDtos.ProjectDto;
import com.saas.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing projects.
 * Projects are containers for todos and are associated with a specific
 * workspace.
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates a new project within a workspace.
     *
     * @param request The project details (name and workspaceId).
     * @return The created ProjectDto.
     */
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectCreateRequest request) {
        log.info("REST request to create project: {} in workspace: {}", request.getName(), request.getWorkspaceId());
        return ResponseEntity.ok(projectService.createProject(request.getWorkspaceId(), request.getName()));
    }

    /**
     * Lists all projects belonging to a specific workspace.
     *
     * @param workspaceId The ID of the workspace.
     * @param pageable    Pagination and sorting information.
     * @return A page of ProjectDtos.
     */
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<Page<ProjectDto>> listProjectsByWorkspace(
            @PathVariable Long workspaceId,
            Pageable pageable) {
        return ResponseEntity.ok(projectService.listProjects(workspaceId, pageable));
    }

    /**
     * Updates an existing project's metadata.
     *
     * @param id      The ID of the project to update.
     * @param request Map containing the new field values (e.g., name).
     * @return The updated ProjectDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @RequestBody Map<String, String> request) {
        log.info("REST request to update project ID: {}", id);
        return ResponseEntity.ok(projectService.updateProject(id, request.get("name")));
    }

    /**
     * Deletes a project by its ID.
     *
     * @param id The ID of the project to delete.
     * @return 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("REST request to delete project ID: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
