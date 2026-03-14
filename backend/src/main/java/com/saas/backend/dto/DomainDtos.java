package com.saas.backend.dto;

import com.saas.backend.enums.TodoPriority;
import com.saas.backend.enums.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class DomainDtos {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkspaceDto {
        private UUID id;
        private String name;
        private UUID organizationId;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectDto {
        private UUID id;
        private String name;
        private UUID workspaceId;
        private UUID organizationId;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodoDto {
        private UUID id;
        private String title;
        private String description;
        private TodoStatus status;
        private TodoPriority priority;
        private UUID assignedUserId;
        private String assignedUserName;
        private UUID projectId;
        private String projectName;
        private UUID organizationId;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodoCreateRequest {
        private String title;
        private String description;
        private TodoStatus status;
        private TodoPriority priority;
        private UUID assignedUserId;
        private UUID projectId;
        private LocalDateTime dueDate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private UUID id;
        private String name;
        private String email;
        private String role;
        private UUID organizationId;
        private LocalDateTime createdAt;
    }

}
