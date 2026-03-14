package com.saas.backend.repository;

import com.saas.backend.model.FileEntity;
import com.saas.backend.security.TenantContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<FileEntity> fileRowMapper = (rs, rowNum) ->
            FileEntity.builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .name(rs.getString("name"))
                    .s3Key(rs.getString("s3_key"))
                    .build();

    public List<FileEntity> findAll() {

        UUID tenantId = TenantContext.getTenantId();

        return jdbcTemplate.query(
                "SELECT * FROM files WHERE org_id = ?",
                fileRowMapper,
                tenantId
        );
    }

    public FileEntity findById(UUID fileId) {

        UUID tenantId = TenantContext.getTenantId();

        return jdbcTemplate.queryForObject(
                "SELECT * FROM files WHERE id = ? AND org_id = ?",
                fileRowMapper,
                fileId,
                tenantId
        );
    }
}