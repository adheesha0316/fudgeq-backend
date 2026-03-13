package com.fudgeq.api.service;

import com.fudgeq.api.dto.AuditLogResponseDto;
import org.springframework.data.domain.Page;

public interface AuditService {
    void logAction(String actor, String action, String description, String targetId, boolean isCritical);
    Page<AuditLogResponseDto> getAllLogs(int page, int size);
    Page<AuditLogResponseDto> getLogsByActor(String actor, int page, int size);
    Page<AuditLogResponseDto> getCriticalLogs(int page, int size);
}
