package com.fudgeq.api.controller;

import com.fudgeq.api.dto.AuditLogResponseDto;
import com.fudgeq.api.dto.StandardResponse;
import com.fudgeq.api.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/audits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminAuditController {

    private final AuditService auditService;

    @GetMapping("/all")
    public ResponseEntity<StandardResponse<Page<AuditLogResponseDto>>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponseDto> logs = auditService.getAllLogs(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("All audit logs retrieved successfully", logs)
        );
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<StandardResponse<Page<AuditLogResponseDto>>> getLogsByUser(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponseDto> userLogs = auditService.getLogsByActor(email, page, size);
        return ResponseEntity.ok(
                StandardResponse.success("Audit logs for user " + email + " retrieved", userLogs)
        );
    }

    @GetMapping("/critical")
    public ResponseEntity<StandardResponse<Page<AuditLogResponseDto>>> getCriticalLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponseDto> criticalLogs = auditService.getCriticalLogs(page, size);
        return ResponseEntity.ok(
                StandardResponse.success("Critical security audit logs retrieved", criticalLogs)
        );
    }
}
