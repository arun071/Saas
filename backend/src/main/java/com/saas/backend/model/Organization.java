package com.saas.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String slug;

    private String plan;

    private Instant createdAt;

    @OneToMany(mappedBy = "organization")
    private List<Workspace> workspaces;

    @OneToMany(mappedBy = "organization")
    private List<Membership> memberships;
}