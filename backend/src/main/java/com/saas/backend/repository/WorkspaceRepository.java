package com.saas.backend.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteWorkspace(UUID workspaceId, UUID tenantId) {

        jdbcTemplate.update(
                "DELETE FROM workspaces WHERE id = ? AND org_id = ?",
                workspaceId,
                tenantId
        );
    }
}