package com.saas.backend.service;

import com.saas.backend.dto.DomainDtos.WorkspaceDto;
import com.saas.backend.entity.Organization;
import com.saas.backend.entity.Workspace;
import com.saas.backend.mapper.EntityMapper;
import com.saas.backend.repository.OrganizationRepository;
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
    private final OrganizationRepository organizationRepository;
    private final EntityMapper entityMapper;

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto createWorkspace(String name) {
        UUID orgId = TenantContext.getCurrentTenant();
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Workspace workspace = Workspace.builder()
                .name(name)
                .organization(organization)
                .build();

        workspace = workspaceRepository.save(workspace);
        log.info("Created workspace: {} (ID: {}) for organization: {}", workspace.getName(), workspace.getId(), orgId);
        return entityMapper.toDto(workspace);
    }

    public Page<WorkspaceDto> listWorkspaces(Pageable pageable) {
        UUID orgId = TenantContext.getCurrentTenant();
        return workspaceRepository.findByOrganization_Id(orgId, pageable)
                .map(entityMapper::toDto);
    }

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public WorkspaceDto updateWorkspace(UUID id, String name) {
        UUID orgId = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        if (!workspace.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        workspace.setName(name);
        Workspace updated = workspaceRepository.save(workspace);
        log.info("Updated workspace ID: {} for organization: {}", id, orgId);
        return entityMapper.toDto(updated);
    }

    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public void deleteWorkspace(UUID id) {
        UUID orgId = TenantContext.getCurrentTenant();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        if (!workspace.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        workspaceRepository.delete(workspace);
        log.info("Deleted workspace ID: {} for organization: {}", id, orgId);
    }
}
