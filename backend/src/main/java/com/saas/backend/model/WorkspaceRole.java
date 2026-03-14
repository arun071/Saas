package com.saas.backend.model;

import com.saas.backend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workspace_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceRole {

    @Id
    @GeneratedValue
    private UUID id;

    private Role role;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}