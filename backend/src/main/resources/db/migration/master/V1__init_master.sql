CREATE TABLE organizations (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME(6)
);
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    organization_id BIGINT,
    created_at DATETIME(6),
    CONSTRAINT fk_user_org FOREIGN KEY (organization_id) REFERENCES organizations(id)
);