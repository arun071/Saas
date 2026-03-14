package com.saas.backend.controller;

import com.saas.backend.dto.DomainDtos.ProjectDto;
import com.saas.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody Map<String, Object> request) {
        log.info("REST request to create project in workspace: {}", request.get("workspaceId"));
        UUID workspaceId = UUID.fromString(request.get("workspaceId").toString());
        String name = request.get("name").toString();
        return ResponseEntity.ok(projectService.createProject(workspaceId, name));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<Page<ProjectDto>> listProjectsByWorkspace(
            @PathVariable UUID workspaceId,
            Pageable pageable) {
        return ResponseEntity.ok(projectService.listProjects(workspaceId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        log.info("REST request to update project ID: {}", id);
        return ResponseEntity.ok(projectService.updateProject(id, request.get("name")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        log.info("REST request to delete project ID: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
