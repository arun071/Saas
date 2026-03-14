package com.saas.backend.controllers;

import com.saas.backend.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PreAuthorize("hasRole('WORKSPACE_ADMIN') or hasRole('ORG_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteWorkspace(@PathVariable UUID id) {
        workspaceService.deleteWorkspace(id);
    }
}