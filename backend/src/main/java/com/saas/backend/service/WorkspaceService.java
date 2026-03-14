package com.saas.backend.service;

import com.saas.backend.repository.WorkspaceRepository;
import com.saas.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public void deleteWorkspace(UUID workspaceId) {

        UUID tenantId = TenantContext.getTenantId();

        workspaceRepository.deleteWorkspace(workspaceId, tenantId);
    }
}