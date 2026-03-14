package com.saas.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String slug;

    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;

    @OneToMany(mappedBy = "workspace")
    private List<Project> projects;

    @OneToMany(mappedBy = "workspace")
    private List<WorkspaceRole> workspaceRoles;
}