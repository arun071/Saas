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
import com.saas.backend.util.SnowflakeIdGenerator;

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
        private final SnowflakeIdGenerator snowflakeIdGenerator;

        /**
         * Creates a new project within a specific workspace.
         *
         * @param workspaceId The ID of the parent workspace.
         * @param name        The name of the project.
         * @return The created ProjectDto.
         */
        public ProjectDto createProject(Long workspaceId, String name) {
                String schemaName = TenantContext.getCurrentTenant();

                Workspace workspace = workspaceRepository.findById(workspaceId)
                                .orElseThrow(() -> new RuntimeException("Workspace not found"));

                Project project = Project.builder()
                                .id(snowflakeIdGenerator.nextId())
                                .name(name)
                                .workspace(workspace)
                                .build();

                project = projectRepository.save(project);
                log.info("Created project: {} (ID: {}) in workspace: {} in schema: {}", project.getName(),
                                project.getId(), workspaceId, schemaName);
                return entityMapper.toDto(project);
        }

        /**
         * Lists all projects in a specific workspace.
         *
         * @param workspaceId The workspace ID.
         * @param pageable    Pagination info.
         * @return Page of ProjectDtos.
         */
        public Page<ProjectDto> listProjects(Long workspaceId, Pageable pageable) {
                return projectRepository.findByWorkspaceId(workspaceId, pageable)
                                .map(entityMapper::toDto);
        }

        /**
         * Updates an existing project.
         *
         * @param id   The ID of the project.
         * @param name The new name.
         * @return The updated ProjectDto.
         */
        public ProjectDto updateProject(Long id, String name) {
                String schemaName = TenantContext.getCurrentTenant();
                Project project = projectRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Project not found"));

                project.setName(name);
                Project updated = projectRepository.save(project);
                log.info("Updated project ID: {} in schema: {}", id, schemaName);
                return entityMapper.toDto(updated);
        }

        /**
         * Deletes a project and its associated todos.
         *
         * @param id The ID of the project to delete.
         */
        public void deleteProject(Long id) {
                String schemaName = TenantContext.getCurrentTenant();
                Project project = projectRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Project not found"));

                projectRepository.delete(project);
                log.info("Deleted project ID: {} in schema: {}", id, schemaName);
        }
}
