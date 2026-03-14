package com.saas.backend.mapper;

import com.saas.backend.dto.DomainDtos.*;
import com.saas.backend.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    WorkspaceDto toDto(Workspace workspace);

    @Mapping(target = "workspaceId", source = "workspace.id")
    @Mapping(target = "organizationId", source = "organization.id")
    ProjectDto toDto(Project project);

    @Mapping(target = "assignedUserId", source = "assignedUser.id")
    @Mapping(target = "assignedUserName", source = "assignedUser.name")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "organizationId", source = "organization.id")
    TodoDto toDto(Todo todo);

    @Mapping(target = "organizationId", source = "organization.id")
    UserDto toDto(User user);
}
