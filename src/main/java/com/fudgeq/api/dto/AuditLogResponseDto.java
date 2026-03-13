package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDto {

    private String auditId;
    private String actor;
    private String action;
    private String description;
    private String targetId;
    private String ipAddress;
    private String userAgent;
    private boolean isCritical;
    private LocalDateTime createdAt; // From BaseEntity
}
