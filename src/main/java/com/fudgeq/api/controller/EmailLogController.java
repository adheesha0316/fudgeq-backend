package com.fudgeq.api.controller;

import com.fudgeq.api.dto.EmailLogResponseDto;
import com.fudgeq.api.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/email-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class EmailLogController {

    private final EmailLogService emailLogService;

    @GetMapping
    public ResponseEntity<Page<EmailLogResponseDto>> getAllEmailLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(emailLogService.getAllEmailLogs(page, size));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<EmailLogResponseDto>> getEmailLogsByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(emailLogService.getEmailLogsByOrderId(orderId));
    }

    @GetMapping("/failed")
    public ResponseEntity<Page<EmailLogResponseDto>> getFailedLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(emailLogService.getFailedEmailLogs(page, size));
    }
}
