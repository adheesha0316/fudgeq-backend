package com.fudgeq.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String auditId;

    @Column(nullable = false, updatable = false)
    private String actor;

    @Column(nullable = false, updatable = false)
    private String action;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String description;

    @Column(updatable = false)
    private String targetId;

    @Column(updatable = false)
    private String ipAddress;

    @Column(updatable = false)
    private String userAgent;

    @Column(name = "is_critical", updatable = false)
    private boolean isCritical = false;

    @Column(name = "log_hash", updatable = false)
    private String logHash;
}
