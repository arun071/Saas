package com.saas.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;

    private String hashedPassword;

    private Instant createdAt;

    @OneToMany(mappedBy = "user")
    private List<Membership> memberships;
}