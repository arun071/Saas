package com.saas.backend.controller;

import com.saas.backend.entity.Organization;
import com.saas.backend.service.TenantProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative controller for system-level operations.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminTenantController {

    private final TenantProvisioningService tenantProvisioningService;

    @PostMapping("/tenants")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Organization> createTenant(@RequestParam String name) {
        log.info("Admin request to create tenant: {}", name);
        Organization organization = tenantProvisioningService.createTenant(name);
        return ResponseEntity.ok(organization);
    }
}
