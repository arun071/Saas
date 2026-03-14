package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.ProjectDto;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.Project;
import com.saas.backend.entity.Workspace;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.OrganizationRepository;
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
    private final OrganizationRepository organizationRepository;
    private final EntityMapper entityMapper;

    public ProjectDto createProject(UUID workspaceId, String name) {
        UUID orgId = TenantContext.getCurrentTenant();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        // Security check: Workspace must belong to the same organization
        if (!workspace.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized workspace access");
        }

        Project project = Project.builder()
                .name(name)
                .workspace(workspace)
                .organization(organization)
                .build();

        project = projectRepository.save(project);
        log.info("Created project: {} (ID: {}) in workspace: {} for organization: {}", project.getName(),
                project.getId(), workspaceId, orgId);
        return entityMapper.toDto(project);
    }

    public Page<ProjectDto> listProjects(UUID workspaceId, Pageable pageable) {
        UUID orgId = TenantContext.getCurrentTenant();
        return projectRepository.findByWorkspaceIdAndOrganization_Id(workspaceId, orgId, pageable)
                .map(entityMapper::toDto);
    }

    public ProjectDto updateProject(UUID id, String name) {
        UUID orgId = TenantContext.getCurrentTenant();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized project access");
        }

        project.setName(name);
        Project updated = projectRepository.save(project);
        log.info("Updated project ID: {} for organization: {}", id, orgId);
        return entityMapper.toDto(updated);
    }

    public void deleteProject(UUID id) {
        UUID orgId = TenantContext.getCurrentTenant();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized project access");
        }

        projectRepository.delete(project);
        log.info("Deleted project ID: {} for organization: {}", id, orgId);
    }
}
