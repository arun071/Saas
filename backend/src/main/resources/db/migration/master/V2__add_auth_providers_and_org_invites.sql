-- Make password nullable to support Google Sign-in
ALTER TABLE saas_db.users
MODIFY password VARCHAR(255) NULL;
-- Add new auth_provider column
ALTER TABLE saas_db.users
ADD COLUMN auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL';
-- Add auth_provider_id column for OAuth
ALTER TABLE saas_db.users
ADD COLUMN auth_provider_id VARCHAR(255) NULL;
-- Add membership_status enum to users
ALTER TABLE saas_db.users
ADD COLUMN membership_status VARCHAR(50) NOT NULL DEFAULT 'APPROVED';
-- Create organization_invites table
CREATE TABLE saas_db.organization_invites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    invite_code VARCHAR(255) NOT NULL UNIQUE,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6),
    expires_at DATETIME(6),
    CONSTRAINT fk_org_invite_org FOREIGN KEY (organization_id) REFERENCES saas_db.organizations(id),
    CONSTRAINT fk_org_invite_user FOREIGN KEY (created_by) REFERENCES saas_db.users(id)
);