package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.ProjectDto;
import com.saas.backend.entity.Project;
import com.saas.backend.entity.Workspace;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.ProjectRepository;
import com.saas.backend.repository.WorkspaceRepository;
import com.saas.backend.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for managing projects within a workspace.
 * Ensures that projects are isolated by organization and correctly linked to
 * workspaces.
 */
@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
@lombok.extern.slf4j.Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final EntityMapper entityMapper;

    public ProjectDto createProject(UUID workspaceId, String name) {
        String schemaName = TenantContext.getCurrentTenant();

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        Project project = Project.builder()
                .name(name)
                .workspace(workspace)
                .build();

        project = projectRepository.save(project);
        log.info("Created project: {} (ID: {}) in workspace: {} in schema: {}", project.getName(),
                project.getId(), workspaceId, schemaName);
        return entityMapper.toDto(project);
    }

    public Page<ProjectDto> listProjects(UUID workspaceId, Pageable pageable) {
        return projectRepository.findByWorkspaceId(workspaceId, pageable)
                .map(entityMapper::toDto);
    }

    public ProjectDto updateProject(UUID id, String name) {
        String schemaName = TenantContext.getCurrentTenant();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(name);
        Project updated = projectRepository.save(project);
        log.info("Updated project ID: {} in schema: {}", id, schemaName);
        return entityMapper.toDto(updated);
    }

    public void deleteProject(UUID id) {
        String schemaName = TenantContext.getCurrentTenant();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
        log.info("Deleted project ID: {} in schema: {}", id, schemaName);
    }
}
