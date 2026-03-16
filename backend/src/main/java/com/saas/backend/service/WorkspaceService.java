package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.WorkspaceDto;
import com.saas.backend.entity.Workspace;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.WorkspaceRepository;
import com.saas.backend.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.saas.backend.util.SnowflakeIdGenerator;

/**
 * Service for managing workspaces.
 * Provides administrative CRUD operations for organization-level containers.
 */
@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
@lombok.extern.slf4j.Slf4j
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final EntityMapper entityMapper;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * Creates a new workspace.
     * Only ORG_ADMINs can create workspaces.
     *
     * @param name The name of the workspace.
     * @return The created WorkspaceDto.
     */
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto createWorkspace(String name) {
        String schemaName = TenantContext.getCurrentTenant();

        Workspace workspace = Workspace.builder()
                .id(snowflakeIdGenerator.nextId())
                .name(name)
                .build();

        workspace = workspaceRepository.save(workspace);
        log.info("Created workspace: {} (ID: {}) in schema: {}", workspace.getName(), workspace.getId(), schemaName);
        return entityMapper.toDto(workspace);
    }

    /**
     * Lists all workspaces in the current tenant.
     *
     * @param pageable Pagination info.
     * @return Page of WorkspaceDtos.
     */
    public Page<WorkspaceDto> listWorkspaces(Pageable pageable) {
        return workspaceRepository.findAll(pageable)
                .map(entityMapper::toDto);
    }

    /**
     * Updates an existing workspace.
     * Only ORG_ADMINs can update workspaces.
     *
     * @param id   The ID of the workspace.
     * @param name The new name.
     * @return The updated WorkspaceDto.
     */
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto updateWorkspace(Long id, String name) {
        String schemaName = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspace.setName(name);
        Workspace updated = workspaceRepository.save(workspace);
        log.info("Updated workspace ID: {} in schema: {}", id, schemaName);
        return entityMapper.toDto(updated);
    }

    /**
     * Deletes a workspace and all its data.
     * Only ORG_ADMINs can delete workspaces.
     *
     * @param id The ID of the workspace to delete.
     */
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public void deleteWorkspace(Long id) {
        String schemaName = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspaceRepository.delete(workspace);
        log.info("Deleted workspace ID: {} in schema: {}", id, schemaName);
    }
}
