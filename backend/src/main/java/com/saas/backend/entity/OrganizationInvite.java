package com.saas.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents an invitation to join an organization.
 * Admins generate these codes to allow new users to join their tenant.
 * This entity resides in the master database (saas_db).
 */
@Entity
@Table(name = "organization_invites", catalog = "saas_db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationInvite {

    /** Serial primary key for the invite record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The organization this invite is for. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** The unique alpha-numeric code used for joining. */
    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    /** The administrator who generated this invite. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /** Timestamp when the invite was created. */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Optional expiration timestamp for the invite code. */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
