package com.fudgeq.api.controller;

import com.fudgeq.api.dto.AuditLogResponseDto;
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
    public ResponseEntity<Page<AuditLogResponseDto>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAllLogs(page, size));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Page<AuditLogResponseDto>> getLogsByUser(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getLogsByActor(email, page, size));
    }

    @GetMapping("/critical")
    public ResponseEntity<Page<AuditLogResponseDto>> getCriticalLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getCriticalLogs(page, size));
    }
}
