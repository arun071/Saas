package com.saas.backend.dto;

import com.saas.backend.enums.TodoPriority;
import com.saas.backend.enums.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Container for Domain-related Data Transfer Objects (Workspaces, Projects,
 * Todos, Users).
 */
public class DomainDtos {

    /** DTO for Workspace details. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkspaceDto {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
    }

    /** DTO for Project details. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectDto {
        private Long id;
        private String name;
        private Long workspaceId;
        private LocalDateTime createdAt;
    }

    /** DTO for detailed Todo item information. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodoDto {
        private Long id;
        private String title;
        private String description;
        private TodoStatus status;
        private TodoPriority priority;
        private Long assignedUserId;
        private String assignedUserName;
        private Long projectId;
        private String projectName;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
    }

    /** DTO for creating or updating a Todo. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodoCreateRequest {
        private String title;
        private String description;
        private TodoStatus status;
        private TodoPriority priority;
        private Long assignedUserId;
        private Long projectId;
        private LocalDateTime dueDate;
    }

    /** DTO for creating or updating a Project. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectCreateRequest {
        private String name;
        private Long workspaceId;
    }

    /** DTO for User profile details within an organization context. */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private Long id;
        private String name;
        private String email;
        private String role;
        private Long organizationId;
        private LocalDateTime createdAt;
    }

}
