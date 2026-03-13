package com.fudgeq.api.entity;

import com.fudgeq.api.enums.AuthProvider;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // Can be null for social login users

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String providerId; // Unique ID from Google/FB

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            // Only MODERATOR requires admin approval.
            // ADMIN, CUSTOMER, and others are auto-approved.
            if (this.role == Role.MODERATOR) {
                this.status = UserStatus.PENDING_APPROVAL;
            } else {
                this.status = UserStatus.APPROVED;
            }
        }
    }
}
