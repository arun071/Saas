CREATE TABLE workspaces (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME(6)
);
CREATE TABLE projects (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    workspace_id BIGINT NOT NULL,
    created_at DATETIME(6),
    CONSTRAINT fk_project_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces(id)
);
CREATE TABLE todos (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    assigned_user_id BIGINT,
    project_id BIGINT NOT NULL,
    due_date DATETIME(6),
    created_at DATETIME(6),
    CONSTRAINT fk_todo_project FOREIGN KEY (project_id) REFERENCES projects(id)
);