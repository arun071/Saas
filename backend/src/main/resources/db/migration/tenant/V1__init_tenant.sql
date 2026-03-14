CREATE TABLE workspaces (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME(6)
);
CREATE TABLE projects (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    workspace_id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    CONSTRAINT fk_project_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id)
);
CREATE TABLE todos (
    id BINARY(16) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    assigned_user_id BINARY(16),
    project_id BINARY(16) NOT NULL,
    due_date DATETIME(6),
    created_at DATETIME(6),
    CONSTRAINT fk_todo_project FOREIGN KEY (project_id) REFERENCES projects(id)
);