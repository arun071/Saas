package com.saas.backend.mapper;

import com.saas.backend.dto.DomainDtos.*;
import com.saas.backend.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between Domain Entities and DTOs.
 * Centralizes the mapping logic for workspaces, projects, todos, and users.
 */
@Mapper(componentModel = "spring")
public interface EntityMapper {

    /**
     * Converts a Workspace entity to WorkspaceDto.
     * 
     * @param workspace The source entity.
     * @return The target DTO.
     */
    WorkspaceDto toDto(Workspace workspace);

    /**
     * Converts a Project entity to ProjectDto, mapping the parent workspace ID.
     * 
     * @param project The source entity.
     * @return The target DTO.
     */
    @Mapping(target = "workspaceId", source = "workspace.id")
    ProjectDto toDto(Project project);

    /**
     * Converts a Todo entity to TodoDto, mapping IDs and names for related
     * entities.
     * 
     * @param todo The source entity.
     * @return The target DTO.
     */
    @Mapping(target = "assignedUserId", source = "assignedUser.id")
    @Mapping(target = "assignedUserName", source = "assignedUser.name")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    TodoDto toDto(Todo todo);

    /**
     * Converts a User entity to UserDto, mapping the organization ID.
     * 
     * @param user The source entity.
     * @return The target DTO.
     */
    @Mapping(target = "organizationId", source = "organization.id")
    UserDto toDto(User user);
}
