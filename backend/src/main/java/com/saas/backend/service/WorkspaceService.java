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

import java.util.UUID;

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

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto createWorkspace(String name) {
        String schemaName = TenantContext.getCurrentTenant();

        Workspace workspace = Workspace.builder()
                .name(name)
                .build();

        workspace = workspaceRepository.save(workspace);
        log.info("Created workspace: {} (ID: {}) in schema: {}", workspace.getName(), workspace.getId(), schemaName);
        return entityMapper.toDto(workspace);
    }

    public Page<WorkspaceDto> listWorkspaces(Pageable pageable) {
        return workspaceRepository.findAll(pageable)
                .map(entityMapper::toDto);
    }

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto updateWorkspace(UUID id, String name) {
        String schemaName = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspace.setName(name);
        Workspace updated = workspaceRepository.save(workspace);
        log.info("Updated workspace ID: {} in schema: {}", id, schemaName);
        return entityMapper.toDto(updated);
    }

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public void deleteWorkspace(UUID id) {
        String schemaName = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        workspaceRepository.delete(workspace);
        log.info("Deleted workspace ID: {} in schema: {}", id, schemaName);
    }
}
